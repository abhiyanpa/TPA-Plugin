package me.errcruze.tPA.utils;

public class Metrics {
    private final Plugin plugin;
    private final int pluginId;

    public Metrics(Plugin plugin, int pluginId) {
        this.plugin = plugin;
        this.pluginId = pluginId;
    }

    public void addCustomChart(SimplePie chart) {
        // Implementation
    }

    public void addCustomChart(SingleLineChart chart) {
        // Implementation
    }

    public static class SimplePie {
        private final String id;
        private final Callable<String> callable;

        public SimplePie(String chartId, Callable<String> callable) {
            this.id = chartId;
            this.callable = callable;
        }
    }

    public static class SingleLineChart {
        private final String id;
        private final Callable<Integer> callable;

        public SingleLineChart(String chartId, Callable<Integer> callable) {
            this.id = chartId;
            this.callable = callable;
        }
    }
}