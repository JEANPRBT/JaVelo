package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Q28_4;

import java.nio.IntBuffer;

/**
 * Enregistrement représentant le tableau de tous les nœuds du graphe JaVelo
 * sous la forme de son seul attribut : la mémoire tampon buffer.
 *
 * @author Jean Perbet (341418)
 * @author Cassio Manuguerra (346232)
 */
public record GraphNodes(IntBuffer buffer) {

    private static final int OFFSET_E = 0;
    private static final int OFFSET_N = OFFSET_E + 1;
    private static final int OFFSET_OUT_EDGES = OFFSET_N + 1;
    private static final int NODE_INTS = OFFSET_OUT_EDGES + 1;

    /**
     * Fonction qui retourne le nombre total de nœuds.
     *
     * @return le nombre total de noeuds
     */
    public int count() {
        return buffer.capacity() / 3;
    }

    /**
     * Fonction qui retourne la coordonnée E du nœud d'identité donnée.
     *
     * @param nodeId l'identité du nœud dans le graphe JaVelo
     * @return la coordonnée E du nœud d'identité nodeId
     */
    public double nodeE(int nodeId) {
        return Q28_4.asDouble(buffer.get(nodeId * NODE_INTS + OFFSET_E));
    }

    /**
     * Fonction qui retourne la coordonnée N du nœud d'identité donnée.
     *
     * @param nodeId l'identité du nœud dans le graphe JaVelo
     * @return la coordonnée N du nœud d'identité nodeId
     */
    public double nodeN(int nodeId) {
        return Q28_4.asDouble(buffer.get(nodeId * NODE_INTS + OFFSET_N));
    }

    /**
     * Fonction qui retourne le nombre d'arêtes sortant du nœud d'identité donnée.
     *
     * @param nodeId l'identité du nœud dans le graphe JaVelo
     * @return le nombre d'arêtes du nœud d'identité nodeId
     */
    public int outDegree(int nodeId) {
        int toExtract = buffer.get(nodeId * NODE_INTS + OFFSET_OUT_EDGES);
        return Bits.extractUnsigned(toExtract, 28, 4);
    }

    /**
     * Fonction qui retourne l'identité de l'edgeIndex-ième
     * arête sortant du nœud d'identité nodeId.
     *
     * @param nodeId    l'identité du nœud dans le graphe JaVelo
     * @param edgeIndex l'index de l'edgeIndex-ième arête sortant du nœud
     * @return l'identité de l'edgeIndex-ième
     * arête sortant du nœud d'identité nodeId.
     * @throws AssertionError quand l'edgeIndex est plus élevé que le nombre de branches sortantes
     */
    public int edgeId(int nodeId, int edgeIndex) {
        assert 0 <= edgeIndex && edgeIndex < outDegree(nodeId);
        int toExtract = buffer.get(nodeId * NODE_INTS + OFFSET_OUT_EDGES);
        return Bits.extractUnsigned(toExtract, 0, 28) + edgeIndex;
    }
}
