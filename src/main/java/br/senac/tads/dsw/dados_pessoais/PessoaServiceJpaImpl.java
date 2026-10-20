package br.senac.tads.dsw.dados_pessoais;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import br.senac.tads.dsw.dados_pessoais.entidade.ConhecimentoEntity;
import br.senac.tads.dsw.dados_pessoais.entidade.PessoaEntity;
import br.senac.tads.dsw.dados_pessoais.repositorio.ConhecimentoRepository;
import br.senac.tads.dsw.dados_pessoais.repositorio.PessoaRepository;
import org.springframework.transaction.annotation.Transactional;

@Primary
@Service
public class PessoaServiceJpaImpl implements PessoaService {

    private final PessoaRepository pessoaRepository;
    private final ConhecimentoRepository conhecimentoRepository;

    public PessoaServiceJpaImpl(PessoaRepository pessoaRepository, ConhecimentoRepository conhecimentoRepository) {
        this.pessoaRepository = pessoaRepository;
        this.conhecimentoRepository = conhecimentoRepository;
    }

    private PessoaDto toDto(PessoaEntity entity) {
        PessoaDto dto = new PessoaDto();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setNome(entity.getNome());
        dto.setEmail(entity.getEmail());
        dto.setTelefone(entity.getTelefone());
        dto.setDataNascimento(entity.getDataNascimento());

        List<String> nomes = new ArrayList<>();
        for (ConhecimentoEntity c : entity.getConhecimentos()) {
            nomes.add(c.getNome());
        }
        dto.setConhecimentos(nomes);
        return dto;
    }

    private PessoaEntity toEntity(PessoaDto dto) {
        PessoaEntity entity = new PessoaEntity();
        entity.setUsername(dto.getUsername());
        entity.setNome(dto.getNome());
        entity.setEmail(dto.getEmail());
        entity.setTelefone(dto.getTelefone());
        entity.setDataNascimento(dto.getDataNascimento());
        entity.setSenha(dto.getSenha());
        if (dto.getConhecimentos() != null) {
            for (String nomeConhecimento : dto.getConhecimentos()) {
                Optional<ConhecimentoEntity> optConh = conhecimentoRepository.findByNomeIgnoreCase(nomeConhecimento);
                if (optConh.isPresent()) {
                    entity.getConhecimentos().add(optConh.get());
                }
            }
        }
        return entity;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PessoaDto> obterPessoas() {
        List<PessoaDto> resultado = new ArrayList<>();
        for (PessoaEntity entity : pessoaRepository.findAll()) {
            PessoaDto dto = toDto(entity);
            resultado.add(dto);
        }
        return resultado;

    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PessoaDto> obterPessoa(String username) {
        Optional<PessoaEntity> optEntity = pessoaRepository.findByUsername(username);
        if (optEntity.isEmpty()) {
            return Optional.empty();

        }
        return Optional.of(toDto(optEntity.get()));
    }

    @Override
    @Transactional
    public PessoaDto incluirNovaPessoa(PessoaDto dto) {
        PessoaEntity entity = toEntity(dto);
        PessoaEntity salva = pessoaRepository.save(entity);
        return toDto(salva);
    }

    @Override
    @Transactional
    public PessoaDto alterarPessoa(String username, PessoaAlteracaoDto pessoaAlteracao) {
        Optional<PessoaEntity> optEntity = pessoaRepository.findByUsername(username);
        if (optEntity.isEmpty()) {
            throw new NaoEncontradoException("Pessoa " + username + " não encontrada");
        }
        PessoaEntity entity = optEntity.get();

        entity.setNome(pessoaAlteracao.getNome());
        entity.setEmail(pessoaAlteracao.getEmail());
        entity.setTelefone(pessoaAlteracao.getTelefone());
        entity.setDataNascimento(pessoaAlteracao.getDataNascimento());
        entity.getConhecimentos().clear();
        if (pessoaAlteracao.getConhecimentos() != null) {
            for (String nomeConhecimento : pessoaAlteracao.getConhecimentos()) {
                Optional<ConhecimentoEntity> optConh = conhecimentoRepository.findByNomeIgnoreCase(nomeConhecimento);
                if (optConh.isPresent()) {
                    entity.getConhecimentos().add(optConh.get());
                }
            }
        }

        PessoaEntity salva = pessoaRepository.save(entity);
        return toDto(salva);
    }

    @Override
    @Transactional
    public void removerPessoa(String username) {
        Optional<PessoaEntity> optEntity = pessoaRepository.findByUsername(username);
        if (optEntity.isEmpty()) {
            throw new NaoEncontradoException("Pessoa " + username + " não encontrada");
        }
        PessoaEntity entity = optEntity.get();
        pessoaRepository.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PessoaDto> buscarPessoas(String termo) {
        List<PessoaDto> resultado = new ArrayList<>();
        for (PessoaEntity entity : pessoaRepository.buscarPorTermo(termo)) {
            resultado.add(toDto(entity));
        }
        return resultado;
    }

}
