/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.osb.www.alloy.editor.config;

import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.ItemSelectorCriterion;
import com.liferay.item.selector.criteria.DownloadURLItemSelectorReturnType;
import com.liferay.item.selector.criteria.URLItemSelectorReturnType;
import com.liferay.item.selector.criteria.image.criterion.ImageItemSelectorCriterion;
import com.liferay.item.selector.criteria.url.criterion.URLItemSelectorCriterion;
import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.editor.configuration.BaseEditorConfigContributor;
import com.liferay.portal.kernel.editor.configuration.EditorConfigContributor;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ResourceBundleLoader;

import java.util.Locale;
import java.util.Map;

import javax.portlet.PortletURL;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Allen Ziegenfus
 */
@Component(
	property = {
		"editor.config.key=fragmenEntryLinkRichTextEditor",
		"javax.portlet.name=" + ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
		"service.ranking:Integer=100"
	},
	service = EditorConfigContributor.class
)
public class FragmentEntryLinkRichTextEditorConfigContributor
	extends BaseEditorConfigContributor {

	@Override
	public void populateConfigJSONObject(
		JSONObject jsonObject, Map<String, Object> inputEditorTaglibAttributes,
		ThemeDisplay themeDisplay,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

		StringBundler sb = new StringBundler(5);

		sb.append(getAllowedContentText());
		sb.append(" a[*](*); div[*](*){text-align}; img[*](*){*}; p[*](*); ");
		sb.append(getAllowedContentLists());
		sb.append(getAllowedContentTable());
		sb.append(" span[*](*){*}; ");

		jsonObject.put(
			"allowedContent", sb.toString()
		).put(
			"enterMode", 2
		).put(
			"extraPlugins", getExtraPluginsLists()
		);

		PortletURL itemSelectorURL = _itemSelector.getItemSelectorURL(
			requestBackedPortletURLFactory, "_EDITOR_NAME_selectItem",
			getImageItemSelectorCriterion(), getURLItemSelectorCriterion());

		jsonObject.put(
			"filebrowserImageBrowseLinkUrl", itemSelectorURL.toString()
		).put(
			"filebrowserImageBrowseUrl", itemSelectorURL.toString()
		).put(
			"removePlugins", getRemovePluginsLists()
		).put(
			"spritemap",
			themeDisplay.getPathThemeImages() + "/lexicon/icons.svg"
		).put(
			"toolbars", getToolbarsJSONObject(themeDisplay.getLocale())
		);
	}

	protected String getAllowedContentLists() {
		return "li ol ul [*](*){*};";
	}

	protected String getAllowedContentTable() {
		return "table[border, cellpadding, cellspacing] {width}; tbody td " +
			"th[scope]; thead tr[scope];";
	}

	protected String getAllowedContentText() {
		return "b code em h1 h2 h3 h4 h5 h6 hr i p pre strong u [*](*){*};";
	}

	protected String getExtraPluginsLists() {
		return "ae_autolink,ae_dragresize,ae_addimages,ae_imagealignment," +
			"ae_placeholder,ae_selectionregion,ae_tableresize," +
				"ae_tabletools,ae_uicore,itemselector,media,adaptivemedia,nbsp";
	}

	protected ItemSelectorCriterion getImageItemSelectorCriterion() {
		ItemSelectorCriterion itemSelectorCriterion =
			new ImageItemSelectorCriterion();

		itemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new DownloadURLItemSelectorReturnType());

		return itemSelectorCriterion;
	}

	protected String getRemovePluginsLists() {
		return "contextmenu,elementspath,floatingspace,image,link,liststyle," +
			"magicline,resize,tabletools,toolbar,ae_embed";
	}

	protected JSONObject getToolbarsJSONObject(Locale locale) {
		JSONObject toolbarJSONObject = JSONUtil.put(
			"buttons", toJSONArray("['image', 'hline']")
		).put(
			"tabIndex", 1
		);

		return JSONUtil.put(
			"add", toolbarJSONObject
		).put(
			"styles", getToolbarsStylesJSONObject(locale)
		);
	}

	protected JSONObject getToolbarsStylesJSONObject(Locale locale) {
		return JSONUtil.put(
			"selections", getToolbarsStylesSelectionsJSONArray(locale)
		).put(
			"tabIndex", 1
		);
	}

	protected JSONArray getToolbarsStylesSelectionsJSONArray(Locale locale) {
		return JSONUtil.putAll(
			getToolbarsStylesSelectionsLinkJSONObject(),
			getToolbarsStylesSelectionsTextJSONObject(locale));
	}

	protected JSONObject getToolbarsStylesSelectionsLinkJSONObject() {
		return JSONUtil.put(
			"buttons", toJSONArray("['linkEditBrowse']")
		).put(
			"name", "link"
		).put(
			"test", "AlloyEditor.SelectionTest.link"
		);
	}

	protected JSONObject getToolbarsStylesSelectionsTextJSONObject(
		Locale locale) {

		return JSONUtil.put(
			"buttons",
			JSONUtil.putAll(
				"bold", "italic", "underline", "ol", "ul", "linkBrowse",
				"removeFormat")
		).put(
			"name", "text"
		).put(
			"test", "AlloyEditor.SelectionTest.text"
		);
	}

	protected ItemSelectorCriterion getURLItemSelectorCriterion() {
		ItemSelectorCriterion itemSelectorCriterion =
			new URLItemSelectorCriterion();

		itemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new URLItemSelectorReturnType());

		return itemSelectorCriterion;
	}

	@Reference
	private ItemSelector _itemSelector;

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(bundle.symbolic.name=com.liferay.frontend.editor.lang)"
	)
	private volatile ResourceBundleLoader _resourceBundleLoader;

}