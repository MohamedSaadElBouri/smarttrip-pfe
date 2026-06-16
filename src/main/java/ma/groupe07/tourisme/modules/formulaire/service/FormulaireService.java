package ma.groupe07.tourisme.modules.formulaire.service;

import lombok.RequiredArgsConstructor;
import ma.groupe07.tourisme.modules.circuit.dto.CircuitDTO;
import ma.groupe07.tourisme.modules.circuit.dto.FormulaireRequest;
import ma.groupe07.tourisme.modules.circuit.service.CircuitService;
import ma.groupe07.tourisme.modules.formulaire.model.QuestionFormulaire;
import ma.groupe07.tourisme.modules.formulaire.repository.QuestionFormulaireRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FormulaireService {

    private final QuestionFormulaireRepository questionRepo;
    private final CircuitService circuitService;

    public List<QuestionFormulaire> getQuestions() {
        return questionRepo.findAllByOrderByOrdreAsc();
    }

    public List<CircuitDTO> soumettre(FormulaireRequest req) {
        return circuitService.recommander(req);
    }
}
