package br.com.alura.runnercircleapi.service;

import br.com.alura.runnercircleapi.exception.RecursoNaoEncontradoException;
import br.com.alura.runnercircleapi.model.Comentario;
import br.com.alura.runnercircleapi.model.Treino;
import br.com.alura.runnercircleapi.model.User;
import br.com.alura.runnercircleapi.repository.ComentarioRepository;
import br.com.alura.runnercircleapi.repository.TreinoRepository;
import br.com.alura.runnercircleapi.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComentarioService {

    private final ComentarioRepository comentarioRepository;
    private final TreinoRepository treinoRepository;
    private final UserRepository userRepository;

    public ComentarioService(ComentarioRepository comentarioRepository, TreinoRepository treinoRepository,
                             UserRepository userRepository) {
        this.comentarioRepository = comentarioRepository;
        this.treinoRepository = treinoRepository;
        this.userRepository = userRepository;
    }

    public List<Comentario> listarPorTreino(Long treinoId) {
        if (!treinoRepository.existsById(treinoId)) {
            throw new RecursoNaoEncontradoException("Treino não encontrado: " + treinoId);
        }
        return comentarioRepository.findByTreinoIdOrderByDataCriacaoAsc(treinoId);
    }

    public Comentario criar(Comentario comentario, Long treinoId, Long userId) {
        Treino treino = treinoRepository.findById(treinoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Treino não encontrado: " + treinoId));
        User autor = userRepository.findById(userId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado: " + userId));
        comentario.setTreino(treino);
        comentario.setAutor(autor);
        return comentarioRepository.save(comentario);
    }
}
