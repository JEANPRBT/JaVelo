package ch.epfl.javelo;

/**
 * Classe fournissant des fonctions afin d'effectuer
 * certains calculs mathématiques spécifiques au
 * calcul d'itinéraires en Suisse dans JaVelo.
 *
 * @author Jean Perbet (341418)
 * @author Cassio Manuguerra (346232)
 */
public final class Math2 {

    private Math2() {}

    /**
     * Fonction qui retourne le plus petit entier supérieur ou égal à x / y
     * (la partie entière par excès de x / y).
     *
     * @param x l'abscisse d'un point
     * @param y l'ordonnée d'un point
     * @return le plus petit entier supérieur ou égal à x / y
     * @throws IllegalArgumentException si x est négatif ou y est négatif ou nul
     */
    public static int ceilDiv(int x, int y) {
        Preconditions.checkArgument(x >= 0 && y > 0);
        return (x + y - 1) / y;
    }

    /**
     * Fonction qui retourne la coordonnée y du point se trouvant sur la droite
     * passant par (0, y0) et (1, y1) et de coordonnée x donnée.
     *
     * @param x  l'abscisse du point dont on recherche l'ordonnée
     * @param y0 l'ordonnée du point de la droite d'abscisse 0
     * @param y1 l'ordonnée du point de la droite d'abscisse 1
     * @return l'ordonnée du point de la droite d'ordonnée x
     * passant par (0, y0) et (1, y1)
     */
    public static double interpolate(double y0, double y1, double x) {
        return Math.fma(y1 - y0, x, y0);
    }

    /**
     * Fonction qui limite la valeur de v dans l'intervalle allant de min à max.
     *
     * @param min la borne min de l'intervalle
     * @param v   la valeur à limiter
     * @param max la borne max de l'intervalle
     * @return la valeur v limitée dans l'intervalle allant de min à max
     * @throws IllegalArgumentException si min > max
     */
    public static int clamp(int min, int v, int max) {
        Preconditions.checkArgument(min <= max);
        if (v < min) return min;
        else return Math.min(v, max);
    }

    /**
     * Fonction qui limite la valeur de v dans l'intervalle allant de min à max.
     *
     * @param min la borne min de l'intervalle
     * @param v   la valeur à limiter
     * @param max la borne max de l'intervalle
     * @return la valeur v limitée dans l'intervalle allant de min à max
     * @throws IllegalArgumentException si min > max
     */
    public static double clamp(double min, double v, double max) {
        Preconditions.checkArgument(min <= max);
        return Math.min(Math.max(v, min), max);
    }


    /**
     * Fonction qui retourne le sinus hyperbolique inverse d'un réel x.
     *
     * @param x nombre réel dont on veut le sinus hyperbolique inverse
     * @return le sinus hyperbolique inverse d'un réel x
     */
    public static double asinh(double x) {
        return Math.log(x + Math.hypot(1, x));
    }

    /**
     * Fonction qui retourne le produit scalaire entre le vecteur u de composantes
     * (uX, uY) et le vecteur v de composantes (vX, vY).
     *
     * @param uX composante x du vecteur u
     * @param uY composante y du vecteur u
     * @param vX composante x du vecteur v
     * @param vY composante y du vecteur v
     * @return le produit scalaire des vecteurs u(uX, uY) et v(vX, vY)
     */
    public static double dotProduct(double uX, double uY, double vX, double vY) {
        return uX * vX + uY * vY;
    }

    /**
     * Fonction qui retourne le carré de la norme du vecteur de composantes (uX, uY).
     *
     * @param uX composante x du vecteur
     * @param uY composante y du vecteur
     * @return la norme au carré du vecteur (uX, uY)
     */
    public static double squaredNorm(double uX, double uY) {
        return dotProduct(uX, uY, uX, uY);
    }

    /**
     * Fonction qui retourne la norme du vecteur de composantes (uX, uY).
     *
     * @param uX composante x du vecteur
     * @param uY composante y du vecteur
     * @return la norme du vecteur (uX, uY)
     */
    public static double norm(double uX, double uY) {
        return Math.sqrt(squaredNorm(uX, uY));
    }

    /**
     * Fonction qui retourne la longueur de la projection orthogonale du vecteur allant du point A(aX, aY)
     * au point P(pX, pY) sur le vecteur allant du point A(aX, aY) au point B(bX, bY).
     *
     * @param aX abscisse du point A
     * @param aY ordonnée du point A
     * @param bX abscisse du point B
     * @param bY ordonnée du point B
     * @param pX abscisse du point P
     * @param pY ordonnée du point P
     * @return la longueur de la projection orthogonale du vecteur AP sur le vecteur AB
     */
    public static double projectionLength(double aX, double aY, double bX, double bY, double pX, double pY) {
        return dotProduct(pX - aX, pY - aY, bX - aX, bY - aY) / norm(bX - aX, bY - aY);
    }
}
