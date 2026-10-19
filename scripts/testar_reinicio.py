"""Teste de retenção executado somente em um banco dedicado terminado em _test."""
import base64
import json
import os
import pathlib
import struct
import subprocess
import time
import urllib.error
import urllib.parse
import urllib.request
import uuid
import zlib

root = pathlib.Path(__file__).resolve().parents[1]
url_banco = os.environ["TEST_DATABASE_URL"]
if not urllib.parse.urlparse(url_banco.removeprefix("jdbc:")).path.endswith("_test"):
    raise RuntimeError("O teste de reinício exige um banco terminado em _test.")
base = "http://127.0.0.1:18080"
ambiente = os.environ.copy()
ambiente.update(SPRING_DATASOURCE_URL=url_banco,
    SPRING_DATASOURCE_USERNAME=os.environ.get("TEST_DATABASE_USERNAME", "oficina_user"),
    SPRING_DATASOURCE_PASSWORD=os.environ.get("TEST_DATABASE_PASSWORD", "oficina_test"),
    PORT="18080", JWT_SECRET="")
jar = next(p for p in (root / "build/libs").glob("*.jar") if not p.name.endswith("-plain.jar"))
log = open(root / "build/reinicio.log", "wb")
processo = None

def request(rota, dados=None, token=None, tipo="application/json"):
    headers = {"Content-Type": tipo}
    if token:
        headers["Authorization"] = "Bearer " + token
    req = urllib.request.Request(base + rota, data=dados, headers=headers)
    with urllib.request.urlopen(req, timeout=10) as res:
        return res.read()

def iniciar():
    global processo
    processo = subprocess.Popen(["java", "-jar", str(jar)], env=ambiente, stdout=log, stderr=log)
    for _ in range(120):
        if processo.poll() is not None:
            raise RuntimeError("A API encerrou antes de ficar pronta. Consulte build/reinicio.log.")
        try:
            if json.loads(request("/health"))["status"] == "UP":
                return
        except (OSError, ValueError):
            pass
        time.sleep(0.5)
    raise RuntimeError("A API não iniciou dentro do prazo.")

def parar():
    global processo
    if processo is not None:
        processo.terminate()
        try:
            processo.wait(timeout=20)
        except subprocess.TimeoutExpired:
            processo.kill()
            processo.wait(timeout=10)
        processo = None

def chunk(tipo, conteudo):
    return struct.pack("!I", len(conteudo)) + tipo + conteudo + struct.pack("!I", zlib.crc32(tipo + conteudo))

png = b"\x89PNG\r\n\x1a\n" + chunk(b"IHDR", struct.pack("!2I5B", 2, 2, 8, 2, 0, 0, 0))
png += chunk(b"IDAT", zlib.compress(b"\x00" + b"\x00" * 6 + b"\x00" + b"\x00" * 6)) + chunk(b"IEND", b"")
try:
    iniciar()
    email = "reinicio-" + uuid.uuid4().hex + "@example.test"
    dados = {"email": email, "senha": "SenhaTeste123!", "tipoUsuario": "PROFISSIONAL", "perfilUsuario": {"nome": "Teste de reinício"}}
    limite = "oficina-" + uuid.uuid4().hex
    corpo = ("--" + limite + '\r\nContent-Disposition: form-data; name="dados"\r\nContent-Type: application/json\r\n\r\n' + json.dumps(dados) + "\r\n").encode()
    corpo += ("--" + limite + '\r\nContent-Disposition: form-data; name="foto"; filename="foto.png"\r\nContent-Type: image/png\r\n\r\n').encode() + png
    corpo += ("\r\n--" + limite + "--\r\n").encode()
    sessao = json.loads(request("/usuario/criar", corpo, tipo="multipart/form-data; boundary=" + limite))
    rota = "/usuario/foto?id=" + str(sessao["usuario"]["id"])
    assert request(rota, token=sessao["token"]) == png
    parar()
    iniciar()
    assert request(rota, token=sessao["token"]) == png
    assert json.loads(request("/usuario/me", token=sessao["token"]))["email"] == email
    resultado = {"reinicio": "aprovado", "foto_persistente": True, "sessao_preservada": True}
    (root / "build/reinicio.json").write_text(json.dumps(resultado, ensure_ascii=False, indent=2))
    print("Reinício aprovado: foto e sessão preservadas no PostgreSQL.")
finally:
    parar()
    log.close()
