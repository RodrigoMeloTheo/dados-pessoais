package br.senac.tads.dsw.dados_pessoais.repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.senac.tads.dsw.dados_pessoais.entidade.UsuarioSistemaEntity;

@Repository
public interface UsuarioSistemaRepository extends JpaRepository<UsuarioSistemaEntity, Integer> {
    Optional<UsuarioSistemaEntity> findByUsername(String username);
}