package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Enregistrement représentant le tableau des 16384 secteurs du graphe JaVelo
 * sous la forme de son seul attribut : la mémoire tampon buffer.
 *
 * @author Jean Perbet (341418)
 * @author Cassio Manuguerra (346232)
 */
public record GraphSectors(ByteBuffer buffer) {

    private static final int OFFSET_NODE_ID = 0;
    private static final int OFFSET_NODE_NUMBER = OFFSET_NODE_ID + Integer.BYTES ;
    private static final int SECTOR_INTS =  OFFSET_NODE_NUMBER + Short.BYTES ;

    /**
     * Fonction qui retourne la liste de tous les secteurs ayant une intersection avec
     * le carré centré au point donné et de côté égal au double de la distance donnée.
     *
     * @param center le PointCh sur lequel le carré est centré
     * @param distance la distance entre le centre du carré et le milieu de chaque côté
     * @return la liste de tous les secteurs qui intersectent le carré de centre donné et de côté
     * égal au double de la distance donnée
     */
    public List<Sector> sectorsInArea(PointCh center, double distance){
        List<Sector> sectorsList = new ArrayList<>();
        int xMin = (int)((center.e() - distance - SwissBounds.MIN_E) / (SwissBounds.WIDTH / 128.0));
        int xMax = (int)((center.e() + distance - SwissBounds.MIN_E) / (SwissBounds.WIDTH / 128.0));
        int yMin = (int)((center.n() - distance - SwissBounds.MIN_N) / (SwissBounds.HEIGHT / 128.0));
        int yMax = (int)((center.n() + distance - SwissBounds.MIN_N) / (SwissBounds.HEIGHT / 128.0));
        for (int i = yMin; i <= yMax ; i++) {
            for (int j = xMin; j <= xMax ; j++) {
                int sectorIndex = 128 * i + j;
                int firstNode = buffer.getInt(sectorIndex * SECTOR_INTS + OFFSET_NODE_ID);
                int endNode = Short.toUnsignedInt(buffer.getShort(sectorIndex * SECTOR_INTS + OFFSET_NODE_NUMBER)) + firstNode + 1;
                sectorsList.add(new Sector(firstNode, endNode));
            }
        }
        return sectorsList ;
    }

    /**
     * Enregistrement imbriqué permettant une représentation plus agréable à utiliser,
     * mais moins compacte, des secteurs.
     */
    public record Sector(int startNodeId, int endNodeId){

    }
}