package br.com.alura.runnercircleapi.service;

import br.com.alura.runnercircleapi.exception.RecursoNaoEncontradoException;
import br.com.alura.runnercircleapi.model.Treino;
import br.com.alura.runnercircleapi.model.User;
import br.com.alura.runnercircleapi.repository.TreinoRepository;
import br.com.alura.runnercircleapi.repository.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public class TreinoService {

    private static final Sort ORDENACAO_PADRAO = Sort.by(Sort.Direction.DESC, "dataCriacao", "id");

    private final TreinoRepository treinoRepository;
    private final UserRepository userRepository;
    private final ImagemStorageService imagemStorageService;

    public TreinoService(TreinoRepository treinoRepository, UserRepository userRepository,
                         ImagemStorageService imagemStorageService) {
        this.treinoRepository = treinoRepository;
        this.userRepository = userRepository;
        this.imagemStorageService = imagemStorageService;
    }

    public Page<Treino> listar(String busca, Pageable pageable) {
        // padrão sempre não nulo: sem busca vira "%%" e casa com qualquer descrição
        String buscaPattern = "%" + (busca == null ? "" : busca.trim()) + "%";
        // ordenação padrão fixa (id desempata timestamps iguais); ignora o sort enviado pelo cliente
        Pageable ordenado = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), ORDENACAO_PADRAO);
        return treinoRepository.findAllComAutor(buscaPattern, ordenado);
    }

    public Optional<Treino> buscarPorId(Long id) {
        return treinoRepository.findById(id);
    }

    public List<Treino> listarPorAutor(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RecursoNaoEncontradoException("Usuário não encontrado: " + userId);
        }
        return treinoRepository.findByAutorId(userId);
    }

    public Treino criar(Treino treino, Long userId, MultipartFile imagem) {
        User autor = userRepository.findById(userId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado: " + userId));
        treino.setAutor(autor);

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

    @Transactional
    public Treino curtir(Long treinoId, Long userId) {
        Treino treino = buscarTreinoOuFalhar(treinoId);
        treino.curtir(buscarUserOuFalhar(userId));
        return treino;
    }

    @Transactional
    public Treino descurtir(Long treinoId, Long userId) {
        Treino treino = buscarTreinoOuFalhar(treinoId);
        treino.descurtir(buscarUserOuFalhar(userId));
        return treino;
    }

    private Treino buscarTreinoOuFalhar(Long id) {
        return treinoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Treino não encontrado: " + id));
    }

    private User buscarUserOuFalhar(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado: " + id));
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
