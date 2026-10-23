"""Consulta vulnerabilidades conhecidas no OSV usando somente nome e versão das bibliotecas."""
from pathlib import Path
import json
import sys
import urllib.request
from datetime import datetime, timezone

def verificar(raiz):
    pasta = raiz / "build" / "seguranca"
    manifesto = pasta / "dependencias.json"
    if not manifesto.is_file():
        print("Gere o manifesto primeiro: gradlew.bat listarDependenciasSeguranca", file=sys.stderr)
        return 2
    deps = json.loads(manifesto.read_text(encoding="utf-8"))
    consulta = {"queries": [{"package": {"ecosystem": "Maven", "name": d["name"]}, "version": d["version"]} for d in deps]}
    req = urllib.request.Request("https://api.osv.dev/v1/querybatch", data=json.dumps(consulta).encode(), headers={"Content-Type": "application/json"}, method="POST")
    with urllib.request.urlopen(req, timeout=45) as resposta:
        resultados = json.load(resposta)["results"]
    if len(resultados) != len(deps):
        raise ValueError("Resposta incompleta do serviço de vulnerabilidades.")
    afetadas = [{**dep, "avisos": [v["id"] for v in r.get("vulns", [])]} for dep, r in zip(deps, resultados) if r.get("vulns")]
    relatorio = {"consultadoEm": datetime.now(timezone.utc).isoformat(), "fonte": "https://osv.dev", "dependencias": len(deps), "afetadas": afetadas}
    (pasta / "vulnerabilidades.json").write_text(json.dumps(relatorio, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    print(f"Bibliotecas consultadas: {len(deps)}. Com avisos conhecidos: {len(afetadas)}.")
    for item in afetadas:
        print(item["name"], item["version"], ", ".join(item["avisos"]))
    print("Relatório: build/seguranca/vulnerabilidades.json. Ausência de avisos não garante ausência de falhas.")
    return 1 if afetadas else 0

if __name__ == "__main__":
    try:
        sys.exit(verificar(Path(__file__).resolve().parents[1]))
    except (OSError, ValueError, KeyError) as erro:
        print("A verificação não foi concluída. Confira a conexão e tente novamente.", file=sys.stderr)
        sys.exit(2)
