package br.sp.etec.sebrae.OficinaAprender.Service.Relation.Permissao;

import br.sp.etec.sebrae.OficinaAprender.Entity.Relation.Permissao.PermissaoAcesso;
import br.sp.etec.sebrae.OficinaAprender.Repository.Relation.Permissao.PermissaoAcessoRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PermissaoAcessoService {

    private static PermissaoAcessoRepository permissaoAcessoRepository;

    public static Optional<PermissaoAcesso> findByID(Long id){return permissaoAcessoRepository.findById(id);}
    public static PermissaoAcesso salvar(PermissaoAcesso pa){return permissaoAcessoRepository.save(pa);}

}
