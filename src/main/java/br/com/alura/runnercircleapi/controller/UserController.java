package br.com.alura.runnercircleapi.controller;

import br.com.alura.runnercircleapi.dto.TreinoResponseDTO;
import br.com.alura.runnercircleapi.mapper.TreinoMapper;
import br.com.alura.runnercircleapi.service.TreinoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@Tag(name = "Usuários", description = "Consultas relacionadas aos usuários do Runner Circle")
public class UserController {

    private final TreinoService treinoService;
    private final TreinoMapper treinoMapper;

    public UserController(TreinoService treinoService, TreinoMapper treinoMapper) {
        this.treinoService = treinoService;
        this.treinoMapper = treinoMapper;
    }

    @GetMapping("/{id}/treinos")
    @Operation(summary = "Lista os treinos de um usuário")
    public List<TreinoResponseDTO> listarTreinos(@PathVariable Long id) {
        return treinoService.listarPorAutor(id).stream()
                .map(treinoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}