package br.sp.etec.sebrae.OficinaAprender.Service;

import br.sp.etec.sebrae.OficinaAprender.Repository.PacienteRepository;
import br.sp.etec.sebrae.OficinaAprender.Entity.Paciente;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class PacienteService {
    private static PacienteRepository pacienteRepository;

    public PacienteService(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    // procurar por ID
    public Optional<Paciente> findByID(Long id) {
        return pacienteRepository.findById(id);
    }

    // Criar Aluno
    public static Paciente criar(Paciente paciente) {
        return pacienteRepository.save(paciente);
    }
    // Editar Aluno
   public static ResponseEntity<?> Criar(Paciente paciente) { return ResponseEntity.ok(pacienteRepository.save(paciente));
    }


}



