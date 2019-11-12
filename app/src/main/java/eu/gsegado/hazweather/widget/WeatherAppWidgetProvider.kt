package eu.gsegado.hazweather.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import eu.gsegado.hazweather.R


class WeatherAppWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        // For each instance of app widget
        appWidgetIds?.size?.let {
            for (i in 0 until it) {
                // We create the hierarchie with the RemoteViews
                val views = RemoteViews(context?.packageName, R.layout.layout_app_widget)

                // Get the current widget id
                val id = appWidgetIds[i]
                // We update all the view of the widget
                appWidgetManager?.updateAppWidget(id, views)
            }
        }
    }
}