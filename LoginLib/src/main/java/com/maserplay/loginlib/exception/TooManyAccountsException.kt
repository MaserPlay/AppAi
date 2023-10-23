package com.maserplay.loginlib.exception

import android.accounts.AccountsException

class TooManyAccountsException : AccountsException("Too many Accounts") {}