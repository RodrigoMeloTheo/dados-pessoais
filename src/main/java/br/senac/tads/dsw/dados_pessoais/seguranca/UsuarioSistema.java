package br.senac.tads.dsw.dados_pessoais.seguranca;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UsuarioSistema implements UserDetails {

    private final String username;
    private final String password;
    private final List<GrantedAuthority> authorities;

    public UsuarioSistema(String username, String password, List<GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    @Override
    public String getUsername() {
        return username;
    }

    // Renomeia "password" para "senhaCodificada" no JSON
    @Override
    @JsonProperty("senhaCodificada")
    public String getPassword() {
        return password;
    }

    // Suprime o campo original "authorities" (que sairia como [{"authority": "..."}])
    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    // Getter: lista de Strings, exposta como "roles"
    @JsonProperty("roles")
    public List<String> getRoles() {
        List<String> roles = new ArrayList<>();
        for (GrantedAuthority a : authorities) {
            roles.add(a.getAuthority());
        }
        return roles;
    }

    // Os quatro campos de estado da conta não aparecem no JSON
    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }
}