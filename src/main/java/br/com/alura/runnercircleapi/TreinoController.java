package br.com.alura.runnercircleapi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/treinos")
@Tag(name = "Treinos", description = "CRUD de treinos (corrida e caminhada) do Runner Circle")
public class TreinoController {

    private final TreinoService treinoService;

    public TreinoController(TreinoService treinoService) {
        this.treinoService = treinoService;
    }

    @GetMapping
    @Operation(summary = "Lista os treinos, com filtro opcional por tipo (CAMINHADA ou CORRIDA)")
    public List<TreinoResponseDTO> listar(@RequestParam(required = false) TipoTreino tipoTreino) {
        return treinoService.listar(tipoTreino);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um treino pelo id")
    public ResponseEntity<TreinoResponseDTO> buscarPorId(@PathVariable Long id) {
        return treinoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Cria um novo treino")
    public ResponseEntity<TreinoResponseDTO> criar(@Valid @RequestBody TreinoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(treinoService.criar(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um treino existente")
    public ResponseEntity<TreinoResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody TreinoRequestDTO dto) {
        return treinoService.atualizar(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um treino")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        if (!treinoService.remover(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
