package br.sp.etec.sebrae.OficinaAprender.Relaction.Service;

import br.sp.etec.sebrae.OficinaAprender.Relaction.Repository.UsuarioAlunoRepository;
import br.sp.etec.sebrae.OficinaAprender.Relaction.UsuarioAluno;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioAlunoService {
    private static UsuarioAlunoRepository usuarioAlunoRepository;

    public Optional<UsuarioAluno> findByID(long id){return usuarioAlunoRepository.findById(id);}

    public UsuarioAluno salvar(UsuarioAluno Ua){
        return usuarioAlunoRepository.save(Ua);
    }

}
