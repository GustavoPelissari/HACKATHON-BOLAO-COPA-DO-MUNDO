package com.unialfa.bolao.config;

import com.unialfa.bolao.domain.*;
import com.unialfa.bolao.repository.PartidaRepository;
import com.unialfa.bolao.repository.SelecaoRepository;
import com.unialfa.bolao.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Popula o banco: cria o admin inicial, as 48 selecoes da Copa de 2026
 * (12 grupos de A a L) e algumas partidas de exemplo em fases variadas.
 * A criacao de selecoes e idempotente (so insere as que ainda nao existem).
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final SelecaoRepository selecaoRepository;
    private final PartidaRepository partidaRepository;
    private final PasswordEncoder passwordEncoder;

    private final boolean seedHabilitado;
    private final String adminNome;
    private final String adminEmail;
    private final String adminSenha;

    public DataSeeder(UsuarioRepository usuarioRepository,
                      SelecaoRepository selecaoRepository,
                      PartidaRepository partidaRepository,
                      PasswordEncoder passwordEncoder,
                      @Value("${app.seed:true}") boolean seedHabilitado,
                      @Value("${app.admin.nome}") String adminNome,
                      @Value("${app.admin.email}") String adminEmail,
                      @Value("${app.admin.senha}") String adminSenha) {
        this.usuarioRepository = usuarioRepository;
        this.selecaoRepository = selecaoRepository;
        this.partidaRepository = partidaRepository;
        this.passwordEncoder = passwordEncoder;
        this.seedHabilitado = seedHabilitado;
        this.adminNome = adminNome;
        this.adminEmail = adminEmail;
        this.adminSenha = adminSenha;
    }

    @Override
    public void run(String... args) {
        if (!seedHabilitado) {
            return;
        }
        criarAdmin();
        criarSelecoes();
        criarPartidasDemo();
    }

    private void criarAdmin() {
        if (usuarioRepository.existsByEmailIgnoreCase(adminEmail)) {
            return;
        }
        Usuario admin = new Usuario();
        admin.setNome(adminNome);
        admin.setEmail(adminEmail.toLowerCase());
        admin.setSenha(passwordEncoder.encode(adminSenha));
        admin.setPerfil(Perfil.ADMIN);
        usuarioRepository.save(admin);
        System.out.println("[SEED] Admin criado: " + adminEmail + " / " + adminSenha);
    }

    /**
     * As 48 selecoes participantes, distribuidas nos 12 grupos (A-L) conforme o sorteio.
     * Faz upsert por codigo FIFA: cria a selecao se nao existir, ou apenas atualiza
     * nome/bandeira/grupo se ja existir (corrige o grupo de execucoes anteriores).
     */
    private void criarSelecoes() {
        // {nome, codigoFIFA, codigoISO (bandeira), grupo}
        String[][] dados = {
                // Grupo A
                {"Mexico", "MEX", "mx", "A"}, {"Coreia do Sul", "KOR", "kr", "A"},
                {"Tchequia", "CZE", "cz", "A"}, {"Africa do Sul", "RSA", "za", "A"},
                // Grupo B
                {"Suica", "SUI", "ch", "B"}, {"Canada", "CAN", "ca", "B"},
                {"Catar", "QAT", "qa", "B"}, {"Bosnia e Herzegovina", "BIH", "ba", "B"},
                // Grupo C
                {"Escocia", "SCO", "gb-sct", "C"}, {"Marrocos", "MAR", "ma", "C"},
                {"Brasil", "BRA", "br", "C"}, {"Haiti", "HAI", "ht", "C"},
                // Grupo D
                {"Estados Unidos", "USA", "us", "D"}, {"Australia", "AUS", "au", "D"},
                {"Turquia", "TUR", "tr", "D"}, {"Paraguai", "PAR", "py", "D"},
                // Grupo E
                {"Alemanha", "GER", "de", "E"}, {"Costa do Marfim", "CIV", "ci", "E"},
                {"Equador", "ECU", "ec", "E"}, {"Curacao", "CUW", "cw", "E"},
                // Grupo F
                {"Suecia", "SWE", "se", "F"}, {"Japao", "JPN", "jp", "F"},
                {"Holanda", "NED", "nl", "F"}, {"Tunisia", "TUN", "tn", "F"},
                // Grupo G
                {"Nova Zelandia", "NZL", "nz", "G"}, {"Ira", "IRN", "ir", "G"},
                {"Belgica", "BEL", "be", "G"}, {"Egito", "EGY", "eg", "G"},
                // Grupo H
                {"Uruguai", "URU", "uy", "H"}, {"Arabia Saudita", "KSA", "sa", "H"},
                {"Espanha", "ESP", "es", "H"}, {"Cabo Verde", "CPV", "cv", "H"},
                // Grupo I
                {"Franca", "FRA", "fr", "I"}, {"Noruega", "NOR", "no", "I"},
                {"Iraque", "IRQ", "iq", "I"}, {"Senegal", "SEN", "sn", "I"},
                // Grupo J
                {"Argentina", "ARG", "ar", "J"}, {"Argelia", "ALG", "dz", "J"},
                {"Austria", "AUT", "at", "J"}, {"Jordania", "JOR", "jo", "J"},
                // Grupo K
                {"Portugal", "POR", "pt", "K"}, {"RD Congo", "COD", "cd", "K"},
                {"Uzbequistao", "UZB", "uz", "K"}, {"Colombia", "COL", "co", "K"},
                // Grupo L
                {"Inglaterra", "ENG", "gb-eng", "L"}, {"Croacia", "CRO", "hr", "L"},
                {"Gana", "GHA", "gh", "L"}, {"Panama", "PAN", "pa", "L"},
        };

        int criadas = 0;
        int atualizadas = 0;
        for (String[] d : dados) {
            Selecao s = selecaoRepository.findByCodigoFifaIgnoreCase(d[1]).orElse(null);
            if (s == null) {
                s = new Selecao();
                s.setCodigoFifa(d[1]);
                criadas++;
            } else {
                atualizadas++;
            }
            s.setNome(d[0]);
            s.setBandeiraUrl("https://flagcdn.com/w160/" + d[2] + ".png");
            s.setGrupo(d[3]);
            selecaoRepository.save(s);
        }

        System.out.println("[SEED] Selecoes: " + criadas + " criadas, " + atualizadas
                + " atualizadas (total: " + selecaoRepository.count() + ").");
    }

    /** Cria partidas de exemplo cobrindo varias fases (apenas se nao houver partidas). */
    private void criarPartidasDemo() {
        if (partidaRepository.count() > 0) {
            return;
        }

        LocalDateTime base = LocalDateTime.now().plusDays(2);

        // Fase de grupos (selecoes do mesmo grupo)
        partidaRepository.save(partida("MEX", "KOR", base, Fase.GRUPOS, "Estadio Azteca", "A"));
        partidaRepository.save(partida("BRA", "MAR", base.plusDays(1), Fase.GRUPOS, "MetLife Stadium", "C"));
        partidaRepository.save(partida("FRA", "NOR", base.plusDays(1).plusHours(3), Fase.GRUPOS, "SoFi Stadium", "I"));
        // Mata-mata (exemplos)
        partidaRepository.save(partida("ARG", "POR", base.plusDays(10), Fase.DEZESSEIS_AVOS, "Hard Rock Stadium", null));
        partidaRepository.save(partida("ESP", "ENG", base.plusDays(14), Fase.OITAVAS, "Lincoln Financial Field", null));
        partidaRepository.save(partida("GER", "BEL", base.plusDays(18), Fase.QUARTAS, "AT&T Stadium", null));
        partidaRepository.save(partida("BRA", "ARG", base.plusDays(22), Fase.SEMI, "Mercedes-Benz Stadium", null));
        partidaRepository.save(partida("FRA", "ESP", base.plusDays(25), Fase.TERCEIRO_LUGAR, "Levi's Stadium", null));
        partidaRepository.save(partida("BRA", "FRA", base.plusDays(26), Fase.FINAL, "MetLife Stadium", null));

        System.out.println("[SEED] Partidas de exemplo criadas em varias fases.");
    }

    private Partida partida(String fifaMandante, String fifaVisitante, LocalDateTime dataHora,
                            Fase fase, String estadio, String grupo) {
        Selecao mandante = selecaoRepository.findAllByOrderByNomeAsc().stream()
                .filter(s -> s.getCodigoFifa().equalsIgnoreCase(fifaMandante)).findFirst().orElseThrow();
        Selecao visitante = selecaoRepository.findAllByOrderByNomeAsc().stream()
                .filter(s -> s.getCodigoFifa().equalsIgnoreCase(fifaVisitante)).findFirst().orElseThrow();

        Partida p = new Partida();
        p.setSelecaoMandante(mandante);
        p.setSelecaoVisitante(visitante);
        p.setDataHora(dataHora);
        p.setFase(fase);
        p.setEstadio(estadio);
        p.setGrupo(grupo);
        p.setStatus(StatusPartida.AGENDADA);
        return p;
    }
}
