## 2025-05-15 - [Widget IPC and Resource Batching]
**Learning:** Updating Android widgets individually via `appWidgetManager.updateAppWidget(Int, RemoteViews)` in a loop causes $O(N)$ Binder IPC calls and redundant resource loading, which is inefficient when multiple widget instances are active.
**Action:** Use `appWidgetManager.updateAppWidget(IntArray, RemoteViews)` for batched updates and cache static resources like string arrays in a companion object to reduce I/O and IPC overhead during frequent events like `USER_PRESENT`.

## 2025-05-20 - [Batch Database Insertions]
**Learning:** Performing multiple individual Room database insertions in a loop creates a new transaction for each operation, leading to $O(N)$ transaction overhead.
**Action:** Use batch insertion methods (e.g., `@Insert suspend fun insertAll(entities: List<T>)`) to wrap all operations in a single transaction, reducing I/O and transactional overhead to $O(1)$. This is especially important during app initialization or pre-population.
