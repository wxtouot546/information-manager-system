/*!
 * Jodit Editor (https://xdsoft.net/jodit/)
 * Licensed under GNU General Public License version 2 or later or a commercial license or MIT;
 * For GPL see LICENSE-GPL.txt in the project root for license information.
 * For MIT see LICENSE-MIT.txt in the project root for license information.
 * For commercial licenses see https://xdsoft.net/jodit/commercial/
 * Copyright (c) 2013-2019 Valeriy Chupurnov. All rights reserved. https://xdsoft.net
 */

import { Component } from '../Component';
import { IPanel, IViewBased } from '../../types/view';
import { Dom } from '../Dom';
import { Create } from '../Create';
import { isJoditObject } from '../helpers/checker/isJoditObject';

export class Panel extends Component implements IPanel {
	protected __whoLocked: string | false = '';
	protected __isFullSize: boolean = false;

	public ownerDocument: Document = document;
	public ownerWindow: Window = window;

	public container: HTMLDivElement;

	/**
	 * @property {Create} Native DOM element creator
	 */
	public create: Create;

	constructor(jodit?: IViewBased) {
		super(jodit);

		if (jodit && jodit.ownerDocument) {
			this.ownerDocument = jodit.ownerDocument;
			this.ownerWindow = jodit.ownerWindow;
		}

		this.create = new Create(
			this.ownerDocument,
			isJoditObject(jodit) ? jodit.editorDocument : undefined
		);

		this.container = this.create.div();
	}

	destruct(): any {
		if (!this.isDestructed) {
			return;
		}

		Dom.safeRemove(this.container);
		delete this.container;
		super.destruct();
	}

	public isLocked = (): boolean => {
		return this.__whoLocked !== '';
	};

	public isLockedNotBy = (name: string): boolean => {
		return this.isLocked() && this.__whoLocked !== name;
	};

	/**
	 * Disable selecting
	 */
	public lock(name: string = 'any') {
		if (!this.isLocked()) {
			this.__whoLocked = name;
			return true;
		}

		return false;
	}

	/**
	 * Enable selecting
	 */
	public unlock() {
		if (this.isLocked()) {
			this.__whoLocked = '';
			return true;
		}

		return false;
	}

	public isFullSize = (): boolean => this.__isFullSize;

	public toggleFullSize(isFullSize?: boolean) {
		if (isFullSize === undefined) {
			isFullSize = !this.__isFullSize;
		}

		if (isFullSize === this.__isFullSize) {
			return;
		}

		this.__isFullSize = isFullSize;
	}
}
