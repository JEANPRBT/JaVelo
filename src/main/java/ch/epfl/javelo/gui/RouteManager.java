package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.routing.RoutePoint;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

/**
 * Classe gérant une partie de l'interaction et l'affichage avec l'itinéraire.
 *
 * @author Jean Perbet (341418)
 * @author Cassio Manuguerra (346232)
 */
public final class RouteManager {

    private final Pane pane ;
    private final Circle circle ;
    private final Polyline polyline ;

    private final RouteBean route ;
    private final ReadOnlyObjectProperty<MapViewParameters> parameters;

    public RouteManager(RouteBean route, ReadOnlyObjectProperty<MapViewParameters> parameters){

        circle = new Circle(5);
        polyline = new Polyline();
        pane = new Pane(polyline, circle);

        this.route = route ;
        this.parameters = parameters ;

        //Le cercle est réglé invisible tant qu'aucun n'itinéraire n'apparaît sur la carte
        circle.setVisible(false);

        //Ajout des identités au circle et la polyline pour les feuilles de style
        circle.setId("highlight");
        polyline.setId("route");

        //Laisser les gestionnaires d'évènement du fond de carte actifs malgré la superposition avec ceux de l'itinéraire
        pane.setPickOnBounds(false);

        //Ajout de listeners aux paramètres de fond de carte et à certains attributs du bean route
        installListeners();

        //Ajout du gestionnaire d'évènement pour le clic sur le cercle
        installCircleHandler();
    }

    /**
     * Méthode retournant le panneau JavaFX contenant l'itinéraire.
     *
     * @return le panneau contenant l'itinéraire
     */
    public Pane pane() {
        return pane ;
    }

    //---------------------------------------------- Private ----------------------------------------------//

    /**
     * Méthode privée permettant d'afficher et de construire ou de rendre invisible
     * la polyLine correspondant à l'itinéraire donné par la propriété route.
     */
    private void updateRoute(){
        if(route.getRoute() == null)
            polyline.setVisible(false);
        else {
            polyline.setVisible(true);
            polyline.getPoints().clear();
            addToPane();
        }
    }

    /**
     * Méthode privée replaçant la polyline après un changement des paramètres x ou y de la carte, au drag de la souris.
     * Elle utilise les anciens paramètres de la carte pour calculer l'ancienne position de l'itinéraire, puis y applique
     * les méthodes viewX et viewY depuis les nouveaux paramètres de la carte pour avoir sa position mise à jour.
     *
     * @param oldValue : les anciens paramètres de la carte
     */
    private void replaceRoute(MapViewParameters oldValue){
        polyline.setLayoutX(polyline.getLayoutX() + oldValue.x() - parameters.get().x());
        polyline.setLayoutY(polyline.getLayoutY() + oldValue.y() - parameters.get().y());
    }

    /**
     * Méthode privée permettant de replacer le cercle et de gérer sa visibilité lorsque l'itinéraire ou les paramètres
     * de fond de carte changent.
     */
    private void updateCircle(){
        if(route.getRoute() == null || Double.isNaN(route.getHighlightedPosition())){
            circle.setVisible(false);
        } else {
            circle.setVisible(true);
            PointCh point = route.getRoute().pointAt(route.getHighlightedPosition());
            circle.setCenterX(parameters.get().viewX(PointWebMercator.ofPointCh(point)));
            circle.setCenterY(parameters.get().viewY(PointWebMercator.ofPointCh(point)));
        }
    }

    /**
     * Méthode privée permettant d'ajouter au panneau une nouvelle polyLine correspondant
     * aux paramètres de carte et à l'itinéraire actuels.
     */
    private void addToPane() {
        for (PointCh point : route.getRoute().points()) {
            double x = PointWebMercator.ofPointCh(point).xAtZoomLevel(parameters.get().zoomLevel());
            double y = PointWebMercator.ofPointCh(point).yAtZoomLevel(parameters.get().zoomLevel());
            polyline.getPoints().addAll(x, y);
        }
        polyline.setLayoutX(-parameters.get().x());
        polyline.setLayoutY(-parameters.get().y());
    }

    /**
     * Méthode privée permettant d'installer des listeners sur certains attributs du bean route et sur les paramètres de
     * fond de carte pour mettre à jour l'itinéraire et sa position si ces derniers changent.
     */
    private void installListeners(){
        route.highlightedPositionProperty().addListener((observable, oldValue, newValue) -> updateCircle());

        route.routeProperty().addListener((observable, oldValue, newValue) -> {
            updateRoute();
            updateCircle();
        });

        parameters.addListener(((observable, oldValue, newValue) -> {
            if(newValue.zoomLevel() != oldValue.zoomLevel()) updateRoute();
            else replaceRoute(oldValue);
            updateCircle();
        }));
    }

    /**
     * Méthode privée permettant de gérer le clic sur le cercle : ajout d'un point de passage intermédiaire sur le nœud de
     * l'itinéraire de plus proche.
     */
    private void installCircleHandler(){
        circle.setOnMouseClicked(event -> {

            Point2D clickInPane = pane.localToParent(circle.getCenterX(), circle.getCenterY());
            PointWebMercator clickInMercator = parameters.get().pointAt((int)clickInPane.getX(), (int)clickInPane.getY());
            RoutePoint clickInRoute = route.getRoute().pointClosestTo(clickInMercator.toPointCh());

            int clickNodeId = route.getRoute().nodeClosestTo(route.getHighlightedPosition());

            route.getWaypoints().add(route.indexOfNonEmptySegmentAt(route.getHighlightedPosition()) + 1,
                                     new Waypoint(clickInRoute.point(), clickNodeId));
        });
    }
}
