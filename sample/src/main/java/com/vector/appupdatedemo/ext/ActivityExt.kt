/*
 * Copyright 2015-2016 Paweł Gajda
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

@file:Suppress("NOTHING_TO_INLINE")

package com.vector.appupdatedemo.ext

import android.app.Activity
import android.support.annotation.IdRes
import android.support.annotation.StringRes
import android.view.View
import android.widget.Toast

inline fun <reified T : View> Activity.find(@IdRes id: Int): T = findViewById(id)

inline fun Activity.toast(text: CharSequence): Unit = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

inline fun Activity.longToast(text: CharSequence): Unit = Toast.makeText(this, text, Toast.LENGTH_LONG).show()

inline fun Activity.toast(@StringRes resId: Int): Unit = Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()

inline fun Activity.longToast(@StringRes resId: Int): Unit = Toast.makeText(this, resId, Toast.LENGTH_LONG).show()
