package grails.plugin.remotessh.executor

class ThreadComparator implements Comparator<ComparableFutureTask> {

	@Override
	public int compare(final ComparableFutureTask lhs, final ComparableFutureTask rhs) {
		
		if(lhs instanceof ComparableFutureTask && rhs instanceof ComparableFutureTask) {
			int returnCode=0
			if (lhs.priority < rhs.priority) {
				returnCode=-1
			} else if (lhs.priority > rhs.priority) {
				returnCode=1
			}
			return returnCode
			
		}
	}
}
