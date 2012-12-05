package com.example.usermanagement;

import com.example.usermanagement.IUpdateListener;

interface IUserSwitchService {
	void addListener(IUpdateListener listener);
	void removeListener(IUpdateListener listener);
}
