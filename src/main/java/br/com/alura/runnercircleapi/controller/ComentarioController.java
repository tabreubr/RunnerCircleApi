package br.com.alura.runnercircleapi.controller;

import br.com.alura.runnercircleapi.dto.ComentarioRequestDTO;
import br.com.alura.runnercircleapi.dto.ComentarioResponseDTO;
import br.com.alura.runnercircleapi.mapper.ComentarioMapper;
import br.com.alura.runnercircleapi.model.Comentario;
import br.com.alura.runnercircleapi.service.ComentarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/treinos/{treinoId}/comentarios")
@Tag(name = "Comentários", description = "Comentários dos treinos do Runner Circle")
public class ComentarioController {

    private final ComentarioService comentarioService;
    private final ComentarioMapper comentarioMapper;

    public ComentarioController(ComentarioService comentarioService, ComentarioMapper comentarioMapper) {
        this.comentarioService = comentarioService;
        this.comentarioMapper = comentarioMapper;
    }

    @GetMapping
    @Operation(summary = "Lista os comentários de um treino")
    public List<ComentarioResponseDTO> listar(@PathVariable Long treinoId) {
        return comentarioService.listarPorTreino(treinoId).stream()
                .map(comentarioMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @PostMapping
    @Operation(summary = "Comenta um treino como o usuário informado em userId (temporário, até existir autenticação)")
    public ResponseEntity<ComentarioResponseDTO> criar(
            @PathVariable Long treinoId,
            @RequestParam Long userId,
            @Valid @RequestBody ComentarioRequestDTO dto) {
        Comentario comentario = comentarioMapper.toEntity(dto);
        Comentario comentarioSalvo = comentarioService.criar(comentario, treinoId, userId);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
        return ResponseEntity.created(location).body(comentarioMapper.toResponseDTO(comentarioSalvo));
    }
}
