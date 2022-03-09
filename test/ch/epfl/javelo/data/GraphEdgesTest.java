package ch.epfl.javelo.data;

import ch.epfl.javelo.Q28_4;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class GraphEdgesTest {
    @Test
    void profileSamplesWorksWithNonCompressedProfiles(){
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10000);
        IntBuffer profileIds = IntBuffer.allocate(1000);
        ShortBuffer elevations = ShortBuffer.allocate(3000);
        for (int i = 0; i < 1000; i++) {
            edgesBuffer.putInt(i);
            edgesBuffer.putShort((short)(3 << 4));
            edgesBuffer.putShort((short)(3 << 4));
            edgesBuffer.putShort((short)(i << 4));
            profileIds.put(1 << 30 | 3*i);
            for (int j = 0; j < 3; j++) {
                elevations.put((short)(200 + i%16 + j));
            }
        }

    }

    @Test
    void profileSamplesWorksWithType0Profiles(){
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        IntBuffer profileIds = IntBuffer.wrap(new int[] {
                0b00010101101010101010010101100011
        });
        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xF000
        });
        GraphEdges graph = new GraphEdges(edgesBuffer, profileIds, elevations);
        assertArrayEquals(new float[]{}, graph.profileSamples(0));
    }


    @Test
    void profileSamplesWorksWithType3Profiles(){
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, ~12);
        // Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 2022
        edgesBuffer.putShort(8, (short) 2022);

        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 3. Index du premier échantillon : 1.
                (3 << 30) | 1
        });

        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xF000
        });

        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);

        assertTrue(edges.isInverted(0));
        assertEquals(12, edges.targetNodeId(0));
        assertEquals(16.6875, edges.length(0));
        assertEquals(16.0, edges.elevationGain(0));
        assertEquals(2022, edges.attributesIndex(0));
        float[] expectedSamples = new float[]{
                384.0625f, 384.125f, 384.25f, 384.3125f, 384.375f,
                384.4375f, 384.5f, 384.5625f, 384.6875f, 384.75f
        };
        assertArrayEquals(expectedSamples, edges.profileSamples(0));
    }

    @Test
    void profileSamplesWorksWithType2Profiles(){
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        //Sens : non inversé. Noeud destination : 12.
        edgesBuffer.putInt(0, 12);
        //Longueur de l'arête : 16m ;
        edgesBuffer.putShort(4, (short) 256);
        //Dénivelé positif : 16m ;
        edgesBuffer.putShort(6, (short) 256);
        edgesBuffer.putShort(8, (short) 2022);

        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                2 << 30
        });

        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0x180C,
                (short) 0x0F0E, (short) 0x0F0E,
                (short) 0x0F0F, (short) 0x0E0E
        });

        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);

        assertFalse(edges.isInverted(0));
        assertEquals(12, edges.targetNodeId(0));
        assertEquals(16.0, edges.length(0));
        assertEquals(16.0, edges.elevationGain(0));
        assertEquals(2022, edges.attributesIndex(0));
        float[] expectedSamples = new float[]{
                384.75f, 384.6875f, 384.5625f, 384.5f, 384.375f,
                384.3125f, 384.25f, 384.125f, 384f
        };
        assertArrayEquals(expectedSamples, edges.profileSamples(0));
    }
}