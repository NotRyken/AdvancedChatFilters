/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatfilters.config.gui;

import fi.dy.masa.malilib.gui.GuiListBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.util.StringUtils;
import io.github.darkkronicle.advancedchatcore.config.gui.widgets.WidgetListRegistryOption;
import io.github.darkkronicle.advancedchatcore.config.gui.widgets.WidgetRegistryOptionEntry;
import io.github.darkkronicle.advancedchatfilters.registry.MatchProcessorRegistry;
import net.minecraft.client.MinecraftClient;

public class GuiFilterProcessors
        extends GuiListBase<
                MatchProcessorRegistry.MatchProcessorOption,
                WidgetRegistryOptionEntry<MatchProcessorRegistry.MatchProcessorOption>,
                WidgetListRegistryOption<MatchProcessorRegistry.MatchProcessorOption>> {

    private final GuiFilterEditor parent;

    public GuiFilterProcessors(GuiFilterEditor parent) {
        super(10, 60);
        this.parent = parent;
        this.setParent(parent.getParent());
        this.title = parent.filter.getName().config.getStringValue();
    }

    @Override
    public void resize(MinecraftClient mc, int width, int height) {
        this.width = width;
        this.height = height;
        this.clearElements();
        this.clearAndInit();
    }

    @Override
    public void initGui() {
        super.initGui();

        int x = 10;
        int y = 26;

        int rows = 1;

        for (GuiFilterEditor.FilterTab tab : GuiFilterEditor.FilterTab.values()) {
            int width = this.getStringWidth(tab.getDisplayName()) + 10;

            if (x >= this.width - width - 10) {
                x = 10;
                y += 22;
                rows++;
            }

            x += this.createTabButton(x, y, width, tab);
        }

        this.setListPosition(this.getListX(), 68 + (rows - 1) * 22);
        this.reCreateListWidget();

        y += 24;
        x = 10;

        this.addButton(x, y, ButtonListener.Type.BACK, false);
        this.getListWidget().refreshEntries();
    }

    protected int addButton(int x, int y, ButtonListener.Type type, boolean rightAlign) {
        ButtonGeneric button = new ButtonGeneric(x, y, -1, rightAlign, type.getDisplayName());
        this.addButton(button, new ButtonListener(type, this));

        return button.getWidth();
    }

    private int createTabButton(int x, int y, int width, GuiFilterEditor.FilterTab tab) {
        ButtonGeneric button = new ButtonGeneric(x, y, width, 20, tab.getDisplayName());
        button.setEnabled(this.parent.tab != tab);
        this.addButton(button, new GuiFilterEditor.ButtonListenerFilterTabs(tab, this.parent));

        return button.getWidth() + 2;
    }

    public void back() {
        closeGui(true);
    }

    @Override
    protected WidgetListRegistryOption<MatchProcessorRegistry.MatchProcessorOption>
            createListWidget(int listX, int listY) {
        return new WidgetListRegistryOption<>(
                listX,
                listY,
                this.getBrowserWidth(),
                this.getBrowserHeight(),
                null,
                parent.filter.getProcessors(),
                this);
    }

    @Override
    protected int getBrowserWidth() {
        return this.width - 20;
    }

    @Override
    protected int getBrowserHeight() {
        return this.height - 6 - this.getListY();
    }

    private static class ButtonListener implements IButtonActionListener {

        private final Type type;
        private final GuiFilterProcessors gui;

        public ButtonListener(Type type, GuiFilterProcessors gui) {
            this.type = type;
            this.gui = gui;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
            if (this.type == Type.BACK) {
                this.gui.back();
            }
        }

        public enum Type {
            BACK("back");

            private static String translate(String key) {
                return "advancedchatfilters.gui.button." + key;
            }

            private final String translationKey;

            Type(String translationKey) {
                this.translationKey = translate(translationKey);
            }

            public String getDisplayName() {
                return StringUtils.translate(this.translationKey);
            }
        }
    }
}
