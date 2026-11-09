package br.senac.tads.dsw.dados_pessoais.seguranca;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.senac.tads.dsw.dados_pessoais.entidade.RoleEntity;
import br.senac.tads.dsw.dados_pessoais.entidade.UsuarioSistemaEntity;
import br.senac.tads.dsw.dados_pessoais.repositorio.UsuarioSistemaRepository;

@Service
public class UsuarioSistemaService implements UserDetailsService {

    private final UsuarioSistemaRepository usuarioRepository;

    public UsuarioSistemaService(UsuarioSistemaRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UsuarioSistemaEntity> optEntity = usuarioRepository.findByUsername(username);
        if (optEntity.isEmpty()) {
            throw new UsernameNotFoundException("Usuário não encontrado: " + username);
        }
        UsuarioSistemaEntity entity = optEntity.get();

        // Converte cada RoleEntity em SimpleGrantedAuthority
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (RoleEntity role : entity.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(role.getNome()));
        }

        return new UsuarioSistema(entity.getUsername(), entity.getSenha(), authorities);
    }
}