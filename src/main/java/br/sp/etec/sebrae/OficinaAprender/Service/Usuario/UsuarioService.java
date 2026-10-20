package br.sp.etec.sebrae.OficinaAprender.Service.Usuario;

import br.sp.etec.sebrae.OficinaAprender.DTO.Login.LoginResponseDTO;
import br.sp.etec.sebrae.OficinaAprender.DTO.Usuario.UsuarioCriarDTO;
import br.sp.etec.sebrae.OficinaAprender.DTO.UsuarioResponseDTO;
import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.PerfilUsuario.PerfilUsuario;
import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.Usuario;
import br.sp.etec.sebrae.OficinaAprender.Repository.Usuario.PerfilUsuario.PerfilUsuarioRepository;
import br.sp.etec.sebrae.OficinaAprender.Repository.Usuario.UsuarioRepository;
import br.sp.etec.sebrae.OficinaAprender.Service.Token.TokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Locale;
import java.util.UUID;

@Service
public class UsuarioService {

    private static final long TAMANHO_MAXIMO_FOTO = 10L * 1024 * 1024;

    private final UsuarioRepository usuarioRepository;
    private final PerfilUsuarioRepository perfilUsuarioRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final Path pastaFotos;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PerfilUsuarioRepository perfilUsuarioRepository,
            TokenService tokenService,
            PasswordEncoder passwordEncoder,
            @Value("${app.uploads.fotos-directory:uploads/perfis}") String pastaFotos
    ) {
        this.usuarioRepository = usuarioRepository;
        this.perfilUsuarioRepository = perfilUsuarioRepository;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
        this.pastaFotos = Path.of(pastaFotos).toAbsolutePath().normalize();
    }

    // Cadastro público. Usuário e perfil são gravados na mesma transação.

    @Transactional
    public LoginResponseDTO criar(UsuarioCriarDTO dto, MultipartFile foto) {
        if (dto == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Informe os dados do cadastro."
            );
        }

        String email = validarEmail(dto.email());
        validarSenha(dto.senha());

        if (dto.perfilUsuario() == null
                || dto.perfilUsuario().getNome() == null
                || dto.perfilUsuario().getNome().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Informe o nome do usuário."
            );
        }

        if (dto.tipoUsuario() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Informe o tipo de usuário."
            );
        }

        if (usuarioRepository.findByEmail(email).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Já existe um usuário com esse e-mail."
            );
        }

        String extensao = validarFoto(foto);
        Path caminhoFoto = salvarFoto(foto, extensao);

        registrarLimpezaEmRollback(caminhoFoto);

        try {
            Usuario usuario = new Usuario();
            usuario.setEmail(email);
            usuario.setSenha(passwordEncoder.encode(dto.senha()));
            usuario.setTipoUsuario(dto.tipoUsuario());

            Usuario usuarioSalvo = usuarioRepository.save(usuario);

            PerfilUsuario perfil = dto.perfilUsuario();
            perfil.setId(null);
            perfil.setUsuario(usuarioSalvo);
            perfil.setNome(perfil.getNome().trim());
            perfil.setFotoArquivo(caminhoFoto.getFileName().toString());
            perfil.setUrlFotoPerfil(
                    "/usuario/foto?id=" + usuarioSalvo.getId()
            );

            perfilUsuarioRepository.saveAndFlush(perfil);

            String token = tokenService.gerarToken(usuarioSalvo);

            UsuarioResponseDTO usuarioDTO =
                    converterParaDTO(usuarioSalvo);

            // Mesmo formato retornado pelo login.
            return new LoginResponseDTO(token, usuarioDTO);

        } catch (RuntimeException erro) {
            removerFoto(caminhoFoto);
            throw erro;
        }
    }

    // Usado pela próxima página para recuperar o usuário autenticado.
    public UsuarioResponseDTO buscarSessao(String authorizationHeader) {
        String email =
                tokenService.validarAutorizacao(authorizationHeader);

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuário não encontrado."
                ));

        return converterParaDTO(usuario);
    }

    public Resource buscarFoto(Long id, String authorizationHeader) {
        tokenService.validarAutorizacao(authorizationHeader);

        PerfilUsuario perfil = perfilUsuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Perfil não encontrado."));
        String nomeArquivo = perfil.getFotoArquivo();

        if (nomeArquivo == null || nomeArquivo.isBlank()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Foto não encontrada.");
        }
        Path caminho = pastaFotos.resolve(nomeArquivo).normalize();
        if (!caminho.startsWith(pastaFotos) || !Files.isRegularFile(caminho)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Foto não encontrada.");
        }
        return new FileSystemResource(caminho);
    }

    public UsuarioResponseDTO findByEmail(String email, String authorizationHeader) {
        tokenService.validarAutorizacao(authorizationHeader);
        Usuario usuario = usuarioRepository.findByEmail(validarEmail(email))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado."));
        return converterParaDTO(usuario);
    }

    public Usuario findById(Long id, String authorizationHeader) {
        tokenService.validarAutorizacao(authorizationHeader);
        return buscarPorId(id);
    }

    @Transactional
    public Usuario editar(Long id, Usuario dadosAtualizados, String authorizationHeader) {
        tokenService.validarAutorizacao(authorizationHeader);
        Usuario usuario = buscarPorId(id);

        if (dadosAtualizados.getEmail() != null) {
            String email = validarEmail(dadosAtualizados.getEmail());
            usuarioRepository.findByEmail(email).ifPresent(outro -> {
                if (!outro.getId().equals(id)) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um usuário com esse e-mail.");
                }
            });
            usuario.setEmail(email);
        }
        if (dadosAtualizados.getSenha() != null && !dadosAtualizados.getSenha().isBlank()) {
            validarSenha(dadosAtualizados.getSenha());
            usuario.setSenha(passwordEncoder.encode(dadosAtualizados.getSenha()));
        }
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void deletar(Long id, String authorizationHeader) {
        tokenService.validarAutorizacao(authorizationHeader);
        usuarioRepository.delete(buscarPorId(id));
    }

    private Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado."));
    }

    public UsuarioResponseDTO converterParaDTO(Usuario usuario) {
        return converterParaDTO(usuario, null);
    }

    public UsuarioResponseDTO converterParaDTO(Usuario usuario, String token) {
        PerfilUsuario perfil = perfilUsuarioRepository.findById(usuario.getId()).orElse(null);
        return new UsuarioResponseDTO(usuario.getId(), usuario.getEmail(), usuario.getTipoUsuario(), perfil, token);
    }

    private String validarEmail(String email) {
        if (email == null || !email.trim().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe um e-mail válido.");
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private void validarSenha(String senha) {
        if (senha == null || senha.length() < 8 || senha.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A senha deve ter ao menos 8 caracteres e no máximo 72 bytes.");
        }
    }

    private String validarFoto(MultipartFile foto) {
        if (foto == null || foto.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Envie uma foto para concluir o cadastro.");
        }
        if (foto.getSize() > TAMANHO_MAXIMO_FOTO) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A foto deve ter até 10 MB.");
        }

        try (var entrada = foto.getInputStream();
             ImageInputStream imagem = ImageIO.createImageInputStream(entrada)) {
            if (imagem == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Foto inválida.");
            }
            Iterator<ImageReader> leitores = ImageIO.getImageReaders(imagem);
            if (!leitores.hasNext()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Envie uma foto PNG, JPEG ou GIF.");
            }
            ImageReader leitor = leitores.next();
            try {
                String formato = leitor.getFormatName().toLowerCase(Locale.ROOT);
                String extensao = switch (formato) {
                    case "png" -> "png";
                    case "jpeg", "jpg" -> "jpg";
                    case "gif" -> "gif";
                    default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Envie uma foto PNG, JPEG ou GIF.");
                };
                leitor.setInput(imagem);
                long pixels = (long) leitor.getWidth(0) * leitor.getHeight(0);
                if (pixels > 25_000_000L || leitor.read(0) == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A foto deve ter até 25 milhões de pixels.");
                }
                return extensao;
            } finally {
                leitor.dispose();
            }
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não foi possível ler a foto.", e);
        }
    }

    private Path salvarFoto(MultipartFile foto, String extensao) {
        Path caminho = pastaFotos.resolve(UUID.randomUUID() + "." + extensao);
        try {
            Files.createDirectories(pastaFotos);
            try (var entrada = foto.getInputStream()) {
                Files.copy(entrada, caminho);
            }
            return caminho;
        } catch (IOException e) {
            removerFoto(caminho);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Não foi possível salvar a foto.", e);
        }
    }

    private void registrarLimpezaEmRollback(Path caminho) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCompletion(int status) {
                    if (status != STATUS_COMMITTED) removerFoto(caminho);
                }
            });
        }
    }

    private void removerFoto(Path caminho) {
        try {
            Files.deleteIfExists(caminho);
        } catch (IOException ignored) {
            // A falha de limpeza não substitui o erro original do cadastro.
        }
    }
}
