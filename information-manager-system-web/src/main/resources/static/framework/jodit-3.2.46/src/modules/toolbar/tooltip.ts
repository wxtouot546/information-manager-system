/*!
 * Jodit Editor (https://xdsoft.net/jodit/)
 * Licensed under GNU General Public License version 2 or later or a commercial license or MIT;
 * For GPL see LICENSE-GPL.txt in the project root for license information.
 * For MIT see LICENSE-MIT.txt in the project root for license information.
 * For commercial licenses see https://xdsoft.net/jodit/commercial/
 * Copyright (c) 2013-2019 Valeriy Chupurnov. All rights reserved. https://xdsoft.net
 */

import { Dom } from '../Dom';
import { setTimeout } from '../helpers/async';
import { ToolbarElement } from './element';
import { IToolbarButton } from '../../types';

/**
 * Class create tooltip for buttons in toolbar
 */
export class ToolbarTooltip extends ToolbarElement {
	private timeout: number = 0;

	private show = () => {
		const showElement = () => {
				this.button.container.appendChild(this.container);

				const diff: number =
					this.container.offsetWidth -
					this.button.container.offsetWidth;
				this.container.style.marginLeft = -diff / 2 + 'px';
			},
			delay: number =
				this.button.jodit.options.showTooltipDelay ||
				this.button.jodit.defaultTimeout * 10;

		this.button.jodit.events.fire('hideTooltip');

		this.timeout = setTimeout(showElement, delay);
	};

	private hide = () => {
		window.clearTimeout(this.timeout);
		Dom.safeRemove(this.container);
	};

	constructor(readonly button: IToolbarButton) {
		super(button.parentToolbar || button.jodit, 'div', 'jodit_tooltip');

		if (button.control.tooltip) {
			this.container.innerHTML = button.tooltipText;

			button.jodit.events
				.on(button.anchor, 'mouseenter', this.show)
				.on(button.anchor, 'mouseleave', this.hide)
				.on(
					'change updateToolbar scroll hidePopup closeAllPopups hideTooltip',
					this.hide
				);
		}
	}

	destruct(): any {
		this.hide();

		if (this.jodit && this.jodit.events) {
			this.jodit.events.off(
				'change updateToolbar scroll hidePopup closeAllPopups hideTooltip',
				this.hide
			);
		}

		return super.destruct();
	}
}
