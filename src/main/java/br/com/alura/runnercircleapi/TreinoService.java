package br.com.alura.runnercircleapi;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TreinoService {

    private final TreinoRepository treinoRepository;

    public TreinoService(TreinoRepository treinoRepository) {
        this.treinoRepository = treinoRepository;
    }

    public List<TreinoResponseDTO> listar(TipoTreino tipoTreino) {
        List<Treino> treinos = treinoRepository.findAll();

        if (tipoTreino != null) {
            treinos = treinos.stream()
                    .filter(treino -> treino.getTipoTreino() == tipoTreino)
                    .collect(Collectors.toList());
        }

        return treinos.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<TreinoResponseDTO> buscarPorId(Long id) {
        return treinoRepository.findById(id).map(this::toDTO);
    }

    public TreinoResponseDTO criar(TreinoRequestDTO dto) {
        Treino treino = treinoRepository.save(toEntity(dto));
        return toDTO(treino);
    }

    public Optional<TreinoResponseDTO> atualizar(Long id, TreinoRequestDTO dto) {
        return treinoRepository.findById(id)
                .map(treino -> {
                    treino.setTipoTreino(dto.tipoTreino());
                    treino.setTempoEmMinutos(dto.tempoEmMinutos());
                    treino.setDistanciaMetros(dto.distanciaMetros());
                    treino.setCalorias(dto.calorias());
                    treino.setBatimentos(dto.batimentos());
                    treino.setDescricao(dto.descricao());
                    return toDTO(treinoRepository.save(treino));
                });
    }

    /**
     * @return true se o treino existia e foi removido, false caso contrário
     */
    public boolean remover(Long id) {
        if (!treinoRepository.existsById(id)) {
            return false;
        }
        treinoRepository.deleteById(id);
        return true;
    }

    private TreinoResponseDTO toDTO(Treino treino) {
        return new TreinoResponseDTO(
                treino.getId(),
                treino.getTipoTreino(),
                treino.getTempoEmMinutos(),
                treino.getDistanciaMetros(),
                treino.getCalorias(),
                treino.getBatimentos(),
                treino.getDescricao(),
                treino.getImagemUrl(),
                treino.getDataCriacao()
        );
    }

    private Treino toEntity(TreinoRequestDTO dto) {
        return new Treino(
                dto.tipoTreino(),
                dto.tempoEmMinutos(),
                dto.distanciaMetros(),
                dto.calorias(),
                dto.batimentos(),
                dto.descricao()
        );
    }
}
