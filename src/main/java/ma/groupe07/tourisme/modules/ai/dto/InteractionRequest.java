package ma.groupe07.tourisme.modules.ai.dto;

import lombok.Data;

/**
 * Signal d'interaction envoye par l'app Android quand l'utilisateur consulte
 * un lieu, un restaurant, un hotel, un monument ou un circuit. Alimente
 * SignalComportement, utilise ensuite par l'IA pour affiner les recommandations.
 */
@Data
public class InteractionRequest {
    /** LIEU ou CIRCUIT */
    private String entiteType;
    private Long entiteId;
    /** VIEW, CLICK, SAVE, COMPLETE, LIKE... */
    private String typeSignal;
}
