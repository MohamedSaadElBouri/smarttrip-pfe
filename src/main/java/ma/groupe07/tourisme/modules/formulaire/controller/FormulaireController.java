package ma.groupe07.tourisme.modules.formulaire.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.groupe07.tourisme.common.ApiResponse;
import ma.groupe07.tourisme.modules.circuit.dto.CircuitDTO;
import ma.groupe07.tourisme.modules.circuit.dto.FormulaireRequest;
import ma.groupe07.tourisme.modules.formulaire.model.QuestionFormulaire;
import ma.groupe07.tourisme.modules.formulaire.service.FormulaireService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/formulaire")
@RequiredArgsConstructor
public class FormulaireController {

    private final FormulaireService formulaireService;

    @GetMapping("/questions")
    public ResponseEntity<ApiResponse<List<QuestionFormulaire>>> getQuestions(
            @RequestParam(defaultValue = "FR") String lang) {
        return ResponseEntity.ok(ApiResponse.success(formulaireService.getQuestions()));
    }

    @PostMapping("/soumettre")
    public ResponseEntity<ApiResponse<List<CircuitDTO>>> soumettre(
            @Valid @RequestBody FormulaireRequest req) {
        List<CircuitDTO> results = formulaireService.soumettre(req);
        return ResponseEntity.ok(ApiResponse.success(
                "Found " + results.size() + " recommended circuits", results));
    }
}
