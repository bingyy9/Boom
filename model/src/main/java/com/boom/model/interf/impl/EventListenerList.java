package com.boom.model.interf.impl;


import com.boom.model.interf.EventListener;

import java.lang.reflect.Array;

public class EventListenerList {
	private final static EventListener[] NULL_ARRAY = new EventListener[0];
	protected transient EventListener[] listenerList = NULL_ARRAY;

	public synchronized EventListener[] getListeners() {
		return listenerList;
	}

	public <T extends EventListener> T[] getListeners(Class<T> t) {
		Object[] lList = listenerList;
		int n = getListenerCount(lList, t);
		T[] result = (T[]) Array.newInstance(t, n);
		int j = 0;
		for (int i = lList.length - 2; i >= 0; i -= 2) {
			if (lList[i] == t) {
				result[j++] = (T) lList[i + 1];
			}
		}
		return result;
	}

	public int getListenerCount(Class<?> t) {
		Object[] lList = listenerList;
		return getListenerCount(lList, t);
	}
	
	private int getListenerCount(Object[] list, Class t) {
		int count = 0;
		for (int i = 0; i < list.length; i += 2) {
			if (t == (Class) list[i])
				count++;
		}
		return count;
	}

	public synchronized int getListenerCount() {
		return listenerList.length;
	}

	public synchronized void set(EventListener evtListener) {  //FixIssue:Memory leak
		if (evtListener == null) {
			return;
		}

		String name = evtListener.getClass().getCanonicalName();
		for (int i = listenerList.length - 1; i >= 0; i --) {
			if (listenerList[i] == null) {
				continue;
			}
			if (name.equals(listenerList[i].getClass().getCanonicalName())) {
				listenerList[i] = evtListener;
				return;
			}
		}

		add(evtListener);  //Not found the listener, add it
	}

	public synchronized int add(EventListener evtListener) {
		if (evtListener == null)
			return -1;

		int idx = -1;
		Object obj = null;
		for (int i = listenerList.length - 1; i >= 0; i--) {
			obj = listenerList[i];
			if (obj != null && obj.equals(evtListener)) {
				idx = i;
				break;
			}
		}

		if (idx != -1)
			return idx;

		if (listenerList == NULL_ARRAY) {
			listenerList = new EventListener[] { evtListener };
			return 0;
		} else {
			int i = listenerList.length;
			EventListener[] tmp = new EventListener[i + 1];
			System.arraycopy(listenerList, 0, tmp, 0, i);
			tmp[i] = evtListener;
			listenerList = tmp;
			return i;
		}
	}

	public synchronized boolean remove(EventListener evtListener) {
		if (evtListener == null) {
			return false;
		}

		int idx = -1;
		int length = listenerList.length - 1;
		for (int i = length; i >= 0; i -= 1) {
//			if ((listenerList[i].equals(evtListener) == true)) {
			if (listenerList[i].equals(evtListener)) {
				idx = i;
				break;
			}
		}

		if (idx == -1)
			return false;

		EventListener[] tmp = new EventListener[length];
		System.arraycopy(listenerList, 0, tmp, 0, idx);
		if (idx < tmp.length) {
			System.arraycopy(listenerList, idx + 1, tmp, idx, tmp.length - idx);
		}

		listenerList = (tmp.length == 0) ? NULL_ARRAY : tmp;
		return true;
	}

	public synchronized void clear() {
		listenerList = NULL_ARRAY;
	}
}
