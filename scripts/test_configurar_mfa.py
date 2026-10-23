import base64
import contextlib
import io
from pathlib import Path
import tempfile
import unittest
from configurar_mfa import configurar

class ConfiguracaoMfaTest(unittest.TestCase):
    def test_gera_sem_exibir_ou_sobrescrever_segredos_existentes(self):
        with tempfile.TemporaryDirectory() as pasta:
            p=Path(pasta)/'.env';p.write_text('JWT_SECRET=exemplo-preservado\nPORT=8181\n')
            saida=io.StringIO()
            with contextlib.redirect_stdout(saida):configurar(p)
            texto=p.read_text();self.assertIn('JWT_SECRET=exemplo-preservado\nPORT=8181\n',texto)
            chave=texto.split('MFA_ENCRYPTION_KEY=',1)[1].strip();self.assertEqual(len(base64.b64decode(chave)),32)
            self.assertNotIn(chave,saida.getvalue())
            with contextlib.redirect_stdout(saida):configurar(p)
            self.assertEqual(texto,p.read_text())
    def test_valor_vazio_nao_engole_variavel_seguinte(self):
        with tempfile.TemporaryDirectory() as pasta:
            p=Path(pasta)/'.env';p.write_text('MFA_ENCRYPTION_KEY=\nPORT=8181\n')
            with contextlib.redirect_stdout(io.StringIO()):configurar(p)
            self.assertIn('\nPORT=8181\n',p.read_text())
            self.assertEqual(p.read_text().count('MFA_ENCRYPTION_KEY='),1)

if __name__=='__main__':unittest.main()
