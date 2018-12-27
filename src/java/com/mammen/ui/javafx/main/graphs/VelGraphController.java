package com.mammen.ui.javafx.graphs;

import com.mammen.settings.DriveBase;
import com.mammen.main.MainUIModel;
import com.mammen.path.Path;
import com.mammen.settings.generator_vars.GeneratorVars;
import com.mammen.settings.SettingsModel;
import com.mammen.settings.Units;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

public class VelGraphController
{
    @FXML
    private LineChart<Double, Double> velGraph;

    @FXML
    private NumberAxis axisTime, axisVel;

    private MainUIModel backend;
    private GeneratorVars vars;


    /**************************************************************************
     *  initialize
     *      Setup gui stuff here.
     *************************************************************************/
    @FXML public void initialize()
    {
        backend = MainUIModel.getInstance();
        vars = SettingsModel.getInstance().getGeneratorVars();

        // Watch this to know when a new path has been generated
        backend.pathProperty().addListener( ( o, oldValue, newValue ) ->
        {
            // Update graph when the path changes
            refresh();
        });

        // Update axis to reflect the new unit
        vars.unitProperty().addListener( ( o, oldValue, newValue ) ->
        {
            updateAxis( newValue );
        });
    }


    public void updateAxis( Units units )
    {
        switch( units )
        {
            case FEET:
                axisVel.setLabel("Velocity (ft/s)");
                break;

            case METERS:
                axisVel.setLabel("Velocity (m/s)");
                break;

            case INCHES:
                axisVel.setLabel("Velocity (in/s)");
                break;
        }
    }


    /**
     * Populates the graph with the newest path data.
     */
    public void refresh()
    {
        XYChart.Series<Double, Double> flSeries, frSeries, blSeries, brSeries;

        // Clear data from velocity graph
        velGraph.getData().clear();

        if( backend.getNumWaypoints() > 1 )
        {
            flSeries = buildSeries( backend.getPath().getFrontLeft() );
            frSeries = buildSeries( backend.getPath().getFrontRight() );

            velGraph.getData().addAll( flSeries, frSeries );

            if( vars.getDriveBase() == DriveBase.SWERVE )
            {
                blSeries = buildSeries( backend.getPath().getBackLeft() );
                brSeries = buildSeries( backend.getPath().getBackRight() );

                velGraph.getData().addAll( blSeries, brSeries );

                flSeries.setName("Front Left Trajectory");
                frSeries.setName("Front Right Trajectory");
                blSeries.setName("Back Left Trajectory");
                brSeries.setName("Back Right Trajectory");
            }
            else
            {
                flSeries.setName("Left Trajectory");
                frSeries.setName("Right Trajectory");
            }
        }
    }

    /**
     * Builds a series from the given trajectory that is ready to display on a LineChart.
     * @param segments Partial path to build a series for.
     * @return The created series to display.
     */
    private static XYChart.Series<Double, Double> buildSeries( Path.Segment[] segments )
    {
        XYChart.Series<Double, Double> series = new XYChart.Series<>();

        if( segments != null )
        {
            int i = 0;
            for( Path.Segment seg : segments )
            {
                // Holds x, y data for a single entry in the series.
                XYChart.Data<Double, Double> data = new XYChart.Data<>();

                // Set the x, y data.
                data.setXValue( seg.dt * i );
                data.setYValue( seg.velocity );

                // Add the data to the series.
                series.getData().add( data );

                i++;
            }
        }

        return series;
    }
}