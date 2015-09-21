package com.arde.media.common.impl;

import javax.enterprise.concurrent.ManageableThread;
import javax.enterprise.concurrent.ManagedThreadFactory;

public class TestManagedThreadFactory implements ManagedThreadFactory {

	private static class TestManageableThread extends Thread implements ManageableThread {

		public TestManageableThread(Runnable r) {
			super(r);
		}

		@Override
		public boolean isShutdown() {
			// TODO Auto-generated method stub
			return false;
		}
		
	}
	@Override
	public Thread newThread(Runnable r) {
		return new TestManageableThread(r);
	}

}
