package com.tiger.yunda.ui.home.inspection.placeholder;

import com.tiger.yunda.ui.home.Mission;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class PlaceholderContent {

    /**
     * An array of sample (placeholder) items.
     */
    public static final List<PlaceholderItem> ITEMS = new ArrayList<PlaceholderItem>();

    /**
     * A map of sample (placeholder) items, by ID.
     */
    public static final Map<String, PlaceholderItem> ITEM_MAP = new HashMap<String, PlaceholderItem>();



    static {
        // Add some sample items.

        addItem(createPlaceholderItem("1", "车头", "巡检车头"));
        addItem(createPlaceholderItem("2", "车左侧", "巡检车左侧"));
        addItem(createPlaceholderItem("3", "车右侧", "巡检车右侧"));
        addItem(createPlaceholderItem("4", "车底", "巡检车底"));
        addItem(createPlaceholderItem("5", "车内", "巡检车内"));

    }

    /**
     * 初始化巡检车位置信息
     * @param mission
     */
    public static void addInspectionPositionData(Mission mission) {
        if (Objects.isNull(mission) || Objects.isNull(mission.getTrainLocations()) || mission.getTrainLocations().isEmpty()) {
            return;
        }
        mission.getTrainLocations().forEach(trainLocations -> {
            addItem(createPlaceholderItem(trainLocations.getId(), trainLocations.getLocationTypeName(), trainLocations.getLocationType() + "_" + trainLocations.getLocationTypeName()));
        });
    }

    private static void addItem(PlaceholderItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static PlaceholderItem createPlaceholderItem(String position, String content,  String details) {
        return new PlaceholderItem(position, content,  details);
    }



    /**
     * A placeholder item representing a piece of content.
     */
    public static class PlaceholderItem {
        public final String id;
        public final String content;
        public final String details;

        public PlaceholderItem(String id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}