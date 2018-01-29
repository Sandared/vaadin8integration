package de.modularco.vaadin.integration.simple.ui;

import com.byteowls.vaadin.chartjs.ChartJs;
import com.byteowls.vaadin.chartjs.config.BarChartConfig;
import com.byteowls.vaadin.chartjs.data.BarDataset;
import com.byteowls.vaadin.chartjs.data.Dataset;
import com.byteowls.vaadin.chartjs.data.LineDataset;
import com.byteowls.vaadin.chartjs.options.Position;
import com.vaadin.server.Sizeable;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import java.util.ArrayList;
import java.util.List;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

@Component(service = UI.class, immediate = true, property = (HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN + "=/graph"))
@SuppressWarnings("all")
public class GraphUI extends UI {
  @Override
  protected void init(final VaadinRequest request) {
    final ChartJs chart = this.getChart();
    chart.setWidth(100, Sizeable.Unit.PERCENTAGE);
    final HorizontalLayout layout = new HorizontalLayout();
    layout.setSizeFull();
    layout.addComponent(chart);
    this.setContent(layout);
  }
  
  public ChartJs getChart() {
    final BarChartConfig config = new BarChartConfig();
    config.data().labels("January", "February", "March", "April", "May", "June", "July").addDataset(new BarDataset().type().label("Dataset 1").backgroundColor("rgba(151,187,205,0.5)").borderColor("white").borderWidth(Integer.valueOf(2))).addDataset(new LineDataset().type().label("Dataset 2").backgroundColor("rgba(151,187,205,0.5)").borderColor("white").borderWidth(2)).addDataset(new BarDataset().type().label("Dataset 3").backgroundColor("rgba(220,220,220,0.5)")).and();
    config.options().responsive(true).title().display(true).position(Position.LEFT).text("Chart.js Combo Bar Line Chart").and().done();
    final List<String> labels = config.data().getLabels();
    List<Dataset<?, ?>> _datasets = config.data().getDatasets();
    for (final Dataset<?, ?> ds : _datasets) {
      {
        final ArrayList<Double> data = new ArrayList<Double>();
        for (int i = 0; (i < labels.size()); i++) {
          double _xifexpression = (double) 0;
          double _random = Math.random();
          boolean _greaterThan = (_random > 0.5);
          if (_greaterThan) {
            _xifexpression = 1.0;
          } else {
            _xifexpression = (-1.0);
          }
          double _random_1 = Math.random();
          double _multiply = (_random_1 * 100);
          long _round = Math.round(_multiply);
          double _multiply_1 = (_xifexpression * _round);
          data.add(Double.valueOf(_multiply_1));
        }
        if ((ds instanceof BarDataset)) {
          final BarDataset bds = ((BarDataset) ds);
          bds.dataAsList(data);
        }
        if ((ds instanceof LineDataset)) {
          final LineDataset lds = ((LineDataset) ds);
          lds.dataAsList(data);
        }
      }
    }
    final ChartJs chart = new ChartJs(config);
    chart.setJsLoggingEnabled(true);
    return chart;
  }
}
