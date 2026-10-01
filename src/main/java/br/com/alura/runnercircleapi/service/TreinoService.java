package br.com.alura.runnercircleapi.service;

import br.com.alura.runnercircleapi.model.TipoTreino;
import br.com.alura.runnercircleapi.model.Treino;
import br.com.alura.runnercircleapi.repository.TreinoRepository;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TreinoService {

    private final TreinoRepository treinoRepository;
    private final ImagemStorageService imagemStorageService;

    public TreinoService(TreinoRepository treinoRepository, ImagemStorageService imagemStorageService) {
        this.treinoRepository = treinoRepository;
        this.imagemStorageService = imagemStorageService;
    }

    public List<Treino> listar(TipoTreino tipoTreino) {
        List<Treino> treinos = treinoRepository.findAll();

        if (tipoTreino != null) {
            treinos = treinos.stream()
                    .filter(treino -> treino.getTipoTreino() == tipoTreino)
                    .collect(Collectors.toList());
        }

        return treinos;
    }

    public Optional<Treino> buscarPorId(Long id) {
        return treinoRepository.findById(id);
    }

    public Treino criar(Treino treino, MultipartFile imagem) {
        if (imagem == null || imagem.isEmpty()) {
            return treinoRepository.save(treino);
        }

        String imagemUrl = imagemStorageService.salvar(imagem);
        treino.setImagemUrl(imagemUrl);
        try {
            return treinoRepository.save(treino);
        } catch (RuntimeException e) {
            imagemStorageService.remover(imagemUrl);
            throw e;
        }
    }

    public Treino atualizar(Long id, Treino treino) {
        treino.setId(id);
        return treinoRepository.save(treino);
    }

    public boolean remover(Long id) {
        if (!treinoRepository.existsById(id)) {
            return false;
        }
        treinoRepository.deleteById(id);
        return true;
    }
}
