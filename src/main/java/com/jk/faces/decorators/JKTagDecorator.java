/*
 * Copyright 2002-2016 Jalal Kiswani.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jk.faces.decorators;

import java.util.List;
import java.util.logging.Logger;

import javax.faces.view.facelets.Tag;
import javax.faces.view.facelets.TagDecorator;

import com.jk.annotations.Author;
import com.jk.faces.config.JKFacesConfigurations;
import com.jk.faces.config.JKNamespace;
import com.jk.faces.config.JKTagMapping;
import com.jk.faces.tags.JKTagAttributeWrapper;
import com.jk.faces.tags.JKTagWrapper;
import com.jk.util.ObjectUtil;

/**
 * <B>JKTagDecorator</B> this class represents the JK implementation for
 * <code>JKTagDecorator</code>.
 *
 * @author Jalal H. Kiswani
 * @see <a href=
 *      "http://docs.oracle.com/javaee/7/api/javax/faces/view/facelets/TagDecorator.html">
 *      TagDecorator</a>
 */
@Author(name = "Jalal Kiswani", date = "3/9/2014", version = "1.0")
public final class JKTagDecorator implements TagDecorator {

	public final static JKTagDecorator Instance = new JKTagDecorator();

	/** The logger. */
	Logger logger = Logger.getLogger(getClass().getName());

	/**
	 * Instantiates a new JK tag decorator.
	 */
	public JKTagDecorator() {
	}

	/**
	 * @param tag
	 *            the tag
	 * @return the tag
	 */
	/**
	 * <code>JK_NAMESPACE</code>.
	 *
	 * @param tag
	 *            the tag
	 * @return the tag
	 * @see com.sun.facelets.tag.TagDecorator#decorate(com.sun.facelets.tag.Tag)
	 */
	@Override
	public Tag decorate(final Tag tag) {
		final JKTagWrapper wrapper = new JKTagWrapper(tag);
		this.logger.fine("decorate tag :".concat(tag.getQName()));
		if (wrapper.isHtmlTag()) {
			this.logger.fine("add missing namespaces");
			addMissingNamespaces(wrapper);
		} else {
			this.logger.fine("handle mapping for tag: " + tag.getQName());
			handleMapping(wrapper);
			if (wrapper.isUrlable()) {
				this.logger.fine("fixing links:" + tag.getQName());
				fixLiks(wrapper);
			}
		}
		return wrapper.buildTag();
	}

	/**
	 * Adds the names spaces.
	 *
	 * @param wrapper
	 *            the tag
	 * @return the tag
	 */
	protected void addMissingNamespaces(final JKTagWrapper wrapper) {
		final JKFacesConfigurations config = JKFacesConfigurations.getInstance();

		final List<JKNamespace> namespaces = config.getNamespaces();
		for (final JKNamespace namespace : namespaces) {
			wrapper.addAttribue(namespace.getPrefix(), namespace.getUrl());
		}
	}

	/**
	 *
	 * @param wrapper
	 */
	protected void fixLiks(final JKTagWrapper wrapper) {
		final List<JKTagAttributeWrapper> links = wrapper.getLinksAttributes();
		for (final JKTagAttributeWrapper link : links) {
			if (link.getValue().startsWith("/") || link.getValue().startsWith("#")) {
				String context = "#{request.contextPath}";
				if (context != null && !context.trim().equals("")) {
					link.setValue(context + "/".concat(link.getValue()));
				}
			}
		}
	}

	/**
	 *
	 * @param wrapper
	 * @return
	 */
	protected void handleMapping(final JKTagWrapper wrapper) {
		final JKFacesConfigurations config = JKFacesConfigurations.getInstance();

		final JKTagMapping mapping = config.findTagMapping(wrapper);
		if (mapping != null) {
			this.logger.fine("mapping found : " + ObjectUtil.toString(mapping));
			final String nameSpaceLetter = mapping.getNameSpaceLetter();
			if (nameSpaceLetter != null) {
				final JKNamespace namespace = config.getNamespaceByLetter(nameSpaceLetter);
				wrapper.setNamespace(namespace.getUrl());
			}
			wrapper.setqName(mapping.getTargetQName());
			wrapper.setLocalName(mapping.getTargetLocalName());
		}
	}

	// protected Tag createTag(Tag oldTag, final List<TagAttribute>
	// newAttributes) {
	// TagAttributesImpl attributes = new
	// TagAttributesImpl(newAttributes.toArray(new TagAttribute[0]));
	// Tag tag = new Tag(oldTag.getLocation(), oldTag.getNamespace(),
	// oldTag.getLocalName(), oldTag.getQName(), attributes);
	// return tag;
	// }

	// protected TagAttributeImpl createAttribute(Tag tag, final String
	// nameSpaceKey, String property) {
	// return new TagAttributeImpl(tag.getLocation(), tag.getNamespace(),
	// nameSpaceKey, nameSpaceKey, property);
	// }

}
