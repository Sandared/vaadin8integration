package de.modularco.vaadin.integration.simple.ui

import com.byteowls.vaadin.chartjs.ChartJs
import com.byteowls.vaadin.chartjs.config.BarChartConfig
import com.byteowls.vaadin.chartjs.data.BarDataset
import com.byteowls.vaadin.chartjs.data.LineDataset
import com.byteowls.vaadin.chartjs.options.Position
import com.vaadin.server.VaadinRequest
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.UI
import java.util.ArrayList
import org.osgi.service.component.annotations.Component
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants

@Component(service=UI, immediate= true, property = HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN + "=/graph" )
class GraphUI extends UI {
	override protected init(VaadinRequest request) {
		val chart = chart
		chart.setWidth(100, Unit.PERCENTAGE)
		val layout = new HorizontalLayout
		layout.setSizeFull
		layout.addComponent(chart)
		content = layout
	}
	
	def getChart(){
		val config = new BarChartConfig();
        config
            .data()
                .labels("January", "February", "March", "April", "May", "June", "July")
                .addDataset(new BarDataset().type().label("Dataset 1").backgroundColor("rgba(151,187,205,0.5)").borderColor("white").borderWidth(2))
                .addDataset(new LineDataset().type().label("Dataset 2").backgroundColor("rgba(151,187,205,0.5)").borderColor("white").borderWidth(2))
                .addDataset(new BarDataset().type().label("Dataset 3").backgroundColor("rgba(220,220,220,0.5)"))
                .and();
        
        config.
            options()
                .responsive(true)
                .title()
                    .display(true)
                    .position(Position.LEFT)
                    .text("Chart.js Combo Bar Line Chart")
                    .and()
               .done();
        
       val labels = config.data().getLabels();
        for (ds : config.data().getDatasets()) {
            val data = new ArrayList<Double>
            for (var i = 0; i < labels.size(); i++) {
            		data.add((if(Math.random > 0.5) 1.0 else -1.0) * Math.round(Math.random *100)
            	)
            }
            
            if (ds instanceof BarDataset) {
                val bds =  ds as BarDataset
                bds.dataAsList(data);    
            }
                
            if (ds instanceof LineDataset) {
                val lds = ds as LineDataset;
                lds.dataAsList(data);    
            }
        }
        
        val chart = new ChartJs(config);
        chart.setJsLoggingEnabled(true);
        return chart
	}
	
}