package br.sp.etec.sebrae.OficinaAprender.Service;

import br.sp.etec.sebrae.OficinaAprender.DTO.LoginRequestDTO;
import br.sp.etec.sebrae.OficinaAprender.DTO.LoginResponseDTO;
import br.sp.etec.sebrae.OficinaAprender.Repository.UsuarioRepository;
import br.sp.etec.sebrae.OficinaAprender.entities.Usuario.Usuario;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UsuarioRepository usuarioRepository;

    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public LoginResponseDTO login(LoginRequestDTO loginIDTO){
        Usuario usuario = usuarioRepository.findByEmail(loginIDTO.email())
                .orElseThrow(() -> new RuntimeException("usuario não encontrado"));
        if(!usuario.getSenha().equals(loginIDTO.senha())){
            throw new RuntimeException("Senha incorreta");
        }
        return  new LoginResponseDTO(
             "login realizado com sucesso",
             usuario.getId(),
             usuario.getEmail(),
             usuario.getRole()
        );
    }


}
