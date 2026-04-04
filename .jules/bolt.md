## 2025-05-15 - [Widget IPC and Resource Batching]
**Learning:** Updating Android widgets individually via `appWidgetManager.updateAppWidget(Int, RemoteViews)` in a loop causes $O(N)$ Binder IPC calls and redundant resource loading, which is inefficient when multiple widget instances are active.
**Action:** Use `appWidgetManager.updateAppWidget(IntArray, RemoteViews)` for batched updates and cache static resources like string arrays in a companion object to reduce I/O and IPC overhead during frequent events like `USER_PRESENT`.
