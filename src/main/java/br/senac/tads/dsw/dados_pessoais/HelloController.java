package br.senac.tads.dsw.dados_pessoais;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;


@RestController 
public class HelloController {

    @GetMapping("/hello")
    public Mensagem hello() {
        return new Mensagem("Rodrigo Melo Theodoro", "Ola, mundo! Meu primeiro endpoint Spring");
    }

    

}
