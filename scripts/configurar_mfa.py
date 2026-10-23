"""Acrescenta a chave do autenticador ao .env local sem exibir ou trocar segredos."""
from pathlib import Path
import base64
import os
import re
import secrets
import argparse

def configurar(caminho):
    texto = caminho.read_text(encoding="utf-8") if caminho.exists() else ""
    encontradas = list(re.finditer(r"^[ \t]*(?:export[ \t]+)?MFA_ENCRYPTION_KEY[ \t]*=[ \t]*([^\r\n]*)$", texto, re.MULTILINE))
    if len(encontradas) > 1:
        raise ValueError("Há definições duplicadas de MFA_ENCRYPTION_KEY. O arquivo foi preservado; confira a configuração antes de gerar a chave.")
    existente = encontradas[0] if encontradas else None
    if existente and existente.group(1).strip("\"'"):
        print("A chave do autenticador já existe. Foi preservada.")
        return
    chave = base64.b64encode(secrets.token_bytes(32)).decode("ascii")
    linha = "MFA_ENCRYPTION_KEY=" + chave
    if existente:
        texto = texto[:existente.start()] + linha + texto[existente.end():]
    else:
        texto += ("\n" if texto and not texto.endswith("\n") else "") + linha + "\n"
    caminho.parent.mkdir(parents=True, exist_ok=True)
    # O_EXCL impede sobrescrever um temporário preexistente; conteúdo nunca vai para stdout.
    temporario = caminho.with_name(caminho.name + ".mfa-" + secrets.token_hex(8))
    fd = os.open(temporario, os.O_WRONLY | os.O_CREAT | os.O_EXCL, 0o600)
    try:
        with os.fdopen(fd, "w", encoding="utf-8", newline="") as arquivo:
            arquivo.write(texto)
        os.replace(temporario, caminho)
    finally:
        temporario.unlink(missing_ok=True)
    print("Chave acrescentada ao .env. Inicie o servidor com esse arquivo carregado e guarde uma cópia segura.")

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--env", type=Path, default=Path(__file__).resolve().parents[1] / ".env", help="Mesmo arquivo .env carregado pelo servidor")
    try:
        configurar(parser.parse_args().env.resolve())
    except (OSError, ValueError) as erro:
        parser.exit(1, "Não foi possível configurar a chave. " + (str(erro) if isinstance(erro, ValueError) else "Confira o caminho e a permissão do arquivo.") + "\n")
