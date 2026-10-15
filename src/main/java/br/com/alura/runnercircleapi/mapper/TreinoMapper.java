package br.com.alura.runnercircleapi.mapper;

import br.com.alura.runnercircleapi.dto.AutorResponseDTO;
import br.com.alura.runnercircleapi.dto.TreinoRequestDTO;
import br.com.alura.runnercircleapi.dto.TreinoResponseDTO;
import br.com.alura.runnercircleapi.model.Treino;
import br.com.alura.runnercircleapi.model.User;

import org.springframework.stereotype.Component;

@Component
public class TreinoMapper {

    public TreinoResponseDTO toResponseDTO(Treino treino) {
        return new TreinoResponseDTO(
                treino.getId(),
                treino.getTipoTreino(),
                treino.getTempoEmMinutos(),
                treino.getDistanciaMetros(),
                treino.getCalorias(),
                treino.getBatimentos(),
                treino.getDescricao(),
                treino.getImagemUrl(),
                treino.getDataCriacao(),
                toAutorDTO(treino.getAutor()),
                treino.getCurtidas().size()
        );
    }

    private AutorResponseDTO toAutorDTO(User autor) {
        if (autor == null) {
            return null;
        }
        return new AutorResponseDTO(autor.getNome(), autor.getUsername(), autor.getAvatarUrl());
    }

    public Treino toEntity(TreinoRequestDTO dto) {
        return new Treino(
                dto.tipoTreino(),
                dto.tempoEmMinutos(),
                dto.distanciaMetros(),
                dto.calorias(),
                dto.batimentos(),
                dto.descricao()
        );
    }

    public void atualizarEntity(Treino treino, TreinoRequestDTO dto) {
        if (dto.tipoTreino() != null) {
            treino.setTipoTreino(dto.tipoTreino());
        }
        if (dto.tempoEmMinutos() != null) {
            treino.setTempoEmMinutos(dto.tempoEmMinutos());
        }
        if (dto.distanciaMetros() != null) {
            treino.setDistanciaMetros(dto.distanciaMetros());
        }
        if (dto.calorias() != null) {
            treino.setCalorias(dto.calorias());
        }
        if (dto.batimentos() != null) {
            treino.setBatimentos(dto.batimentos());
        }
        if (dto.descricao() != null) {
            treino.setDescricao(dto.descricao());
        }
    }
}
