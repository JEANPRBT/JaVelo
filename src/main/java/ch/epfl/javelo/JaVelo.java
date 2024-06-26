package ch.epfl.javelo;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.gui.AnnotatedMapManager;
import ch.epfl.javelo.gui.ElevationProfileManager;
import ch.epfl.javelo.gui.ErrorManager;
import ch.epfl.javelo.gui.RouteBean;
import ch.epfl.javelo.gui.TileManager;
import ch.epfl.javelo.routing.CityBikeCF;
import ch.epfl.javelo.routing.CostFunction;
import ch.epfl.javelo.routing.GpxGenerator;
import ch.epfl.javelo.routing.RouteComputer;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class JaVelo extends Application {

    private static final int MINIMUM_WINDOW_WIDTH = 800;
    private static final int MINIMUM_WINDOW_HEIGHT = 600;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        //Création des éléments indispensables à la création des différents gestionnaires graphiques
        Path cacheBasePath = Paths.get(System.getProperty("user.home"), ".javelo-cache");
        if (!Files.exists(cacheBasePath)) {
            Files.createDirectories(cacheBasePath);
        }
        String tileServerHost = "tile.openstreetmap.org";
        TileManager tileManager = new TileManager(cacheBasePath, tileServerHost);

        Graph graph = Graph.loadFrom("ch_west");
        CostFunction costFunction = new CityBikeCF(graph);
        RouteBean routeBean = new RouteBean(new RouteComputer(graph, costFunction));

        //Création des gestionnaires graphiques
        ErrorManager errorManager = new ErrorManager();
        AnnotatedMapManager map = new AnnotatedMapManager(graph, tileManager, routeBean, errorManager::displayError);
        ElevationProfileManager profile = new ElevationProfileManager(routeBean.elevationProfileProperty(),
                                                                      routeBean.highlightedPositionProperty());

        //Création du splitPane qui contiendra la carte annotée et le profil
        SplitPane splitPane = new SplitPane(map.pane());
        splitPane.setOrientation(Orientation.VERTICAL);
        SplitPane.setResizableWithParent(profile.pane(), false);

        //Création de la barre de menus permettant d'exporter l'itinéraire au format GPX
        MenuItem menuItem = new MenuItem("Exporter GPX");
        Menu menu = new Menu("Fichier");
        menu.getItems().add(menuItem);
        MenuBar menuBar = new MenuBar(menu);

        //Paramétrage de l'action à effectuer lors du clic sur le sous-menu
        menuItem.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer l'itinéraire au format GPX");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("GPX Files", "*.gpx"));
            File file = fileChooser.showSaveDialog(stage);
            if (file != null) {
                try {
                    GpxGenerator.writeGpx(file.toPath(), routeBean.getRoute(), routeBean.getElevationProfile());
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        });

        /* Lien entre la position mise en évidence et :
            - la position de la souris sur l'itinéraire si celle-ci est >= 0
            - la position de la souris sur le profil sinon  */
        routeBean.highlightedPositionProperty().bind(Bindings.when(map.mousePositionOnRouteProperty().greaterThanOrEqualTo(0))
                                                             .then(map.mousePositionOnRouteProperty())
                                                             .otherwise(profile.mousePositionOnProfileProperty()));


        // Lien entre la possibilité de cliquer sur le sous-menu "Export GPX" et l'existence d'un itinéraire
        menuItem.disableProperty().bind(Bindings.isNull(routeBean.routeProperty()));

        //Listener sur l'itinéraire pour ajouter ou retirer le profil en long du panneau principal en fonction de son existence
        routeBean.routeProperty().addListener((o, oldS, newS) -> {
            if (newS == null)
                splitPane.getItems().remove(profile.pane());
            if (oldS == null && newS != null)
                splitPane.getItems().add(profile.pane());
        });

        /* Création du panneau central de la scène, un stackPane avec
        la carte annotée, le profil et le gestionnaire graphique d'erreurs */
        StackPane stackPane = new StackPane(splitPane, errorManager.pane()) ;


        //Création du panneau principal contenant le panneau central et la barre des menus sur le bord supérieur.
        BorderPane mainPane = new BorderPane(stackPane, menuBar, null, null, null);

        //Lancement de la scène
        stage.setMinWidth(MINIMUM_WINDOW_WIDTH);
        stage.setMinHeight(MINIMUM_WINDOW_HEIGHT);
        stage.setTitle("JaVelo");
        stage.setScene(new Scene(mainPane));
        stage.show();
    }
}
