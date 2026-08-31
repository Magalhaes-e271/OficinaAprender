package br.sp.etec.sebrae.OficinaAprender.Service.Usuario.PerfilUsuario;

import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.PerfilUsuario.PerfilUsuario;
import br.sp.etec.sebrae.OficinaAprender.Repository.Usuario.PerfilUsuario.PerfilUsuarioRepository;
import br.sp.etec.sebrae.OficinaAprender.Service.Usuario.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PerfilUsuarioService {

    private final PerfilUsuarioRepository perfilUsuarioRepository;
    private final UsuarioService usuarioService;

    public PerfilUsuarioService(PerfilUsuarioRepository perfilUsuarioRepository, UsuarioService usuarioService) {
        this.perfilUsuarioRepository = perfilUsuarioRepository;
        this.usuarioService = usuarioService;
    }

    public Optional<PerfilUsuario> findByID(Long id) { return perfilUsuarioRepository.findById(id); }

    public PerfilUsuario salvar(Long id, PerfilUsuario dto) {

        PerfilUsuario perfil = perfilUsuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Perfil não encontrado"));

        if (dto.getNome() != null)
            perfil.setNome(dto.getNome());

        if (dto.getTelefone() != null)
            perfil.setTelefone(dto.getTelefone());

        if (dto.getCpf() != null)
            perfil.setCpf(dto.getCpf());

        if (dto.getUrlFotoPerfil() != null)
            perfil.setUrlFotoPerfil(dto.getUrlFotoPerfil());

        if (dto.getDataNascimento() != null)
            perfil.setDataNascimento(dto.getDataNascimento());

        if (dto.getOcupacao() != null)
            perfil.setOcupacao(dto.getOcupacao());

        if (dto.getInstituicao() != null)
            perfil.setInstituicao(dto.getInstituicao());

        if (dto.getConteudoHtml() != null)
            perfil.setConteudoHtml(dto.getConteudoHtml());

        return perfilUsuarioRepository.save(perfil);
    }

    public ResponseEntity<?> criar(PerfilUsuario perfilUsuario) {
        return ResponseEntity.ok(perfilUsuarioRepository.save(perfilUsuario));
    }

}
