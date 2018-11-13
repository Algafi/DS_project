package core.ds.ds_project;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;


/**
 * Not necessary all ready understandable.
 */
public abstract class TaskDecorator extends Task {
    /**
     * Not necessary all ready understandable.
     */
    protected Task task = null;

    /**
     * Not necessary all ready understandable.
     * @param task1 .
     */
    public TaskDecorator(final Task task1) {
        super(task1.name, task1.description);
        this.task = task1;
        task.setHigherLayerDecorator(this);
    }
    /**
     * Not necessary all ready understandable.
     * @return the intervals of a task
     */
    public Collection<Interval> getIntervals() {
        return task.getIntervals();
    }

    /**
     * Add's an interval to the interval array passing itself as a parent.
     *
     * @param name Name of the interval for debug purposes.
     * @return The interval created.
     */
    @Override
    public Interval addInterval(final String name) {
        return task.addInterval(name);
    }
    /**
     * Not necessary all ready understandable.
     */
    @Override
    public Duration stopLastInterval() {
        return task.stopLastInterval();
    }
    /**
     * Not necessary all ready understandable.
     */
    @Override
    public void acceptVisitor(final Visitor visitor) {
        task.acceptVisitor(visitor);
    }


    /**
     * Method that obtains the activity duration of the job
     * within a range defined by two Dates.
     *
     * @param fromDate Date that sets the beginning of the range.
     * @param toDate Date that sets the end of the range.
     * @return [Duration] Returns the duration in the specified range.
     */
    @Override
    public Duration getDurationInRange(final LocalDateTime fromDate,
                                       final LocalDateTime toDate) {
        return task.getDurationInRange(fromDate, toDate);
    }

}
