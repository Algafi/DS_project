package core.ds.ds_project;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Core implementation of a Task.
 */
public class BasicTask extends Task {

    /**
     * Array of intervals.
     */
    private Collection<Interval> intervals = new ArrayList<>();

    /**
     * The runningInterval.
     */
    private Interval runningInterval = null;

    /**
     * Logger of the class BasicTask.
     */
    private final Logger log = LoggerFactory.getLogger(BasicTask.class);


    /**
     * Constructor of the class, only calls the super constructor.
     *
     * @param name Name of the job.
     * @param description What will be the job about.
     */
    public BasicTask(final String name, final String description) {

        super(name, description);
        invariant();
    }

    /**
     * Invariant of the BasicTask class.
     */
    private void invariant() {
        assert (this.getDescription() != null) : "Illegal null description";
        assert (this.getName() != null) : "Illegal null name";
        assert (this.getDuration() != null) : "Illegal null duration";
        assert (this.intervals != null) : "Intervals can't be null";
    }

    /**
     * Get the intervals.
     *
     * @return all the intervals
     */
    public Collection<Interval> getIntervals() {
        return intervals;
    }

    /**
     * Add's an interval to the interval array passing itself as a parent.
     *
     * @param name Name of the interval for debug purposes.
     * @return The interval created.
     */
    public Interval addInterval(final String name) {

        /**
         * Class that save the current state
         * of postcondition variables.
         */
        class DataCopy {
            private int numIntervals;
            /**
             * Saves the data in the current state.
             * @param numIntervalsCpy We want to save the size of the array.
             */
            DataCopy(final int numIntervalsCpy) {
                numIntervals = numIntervalsCpy;
            }
            /**
             * Check if the array has extended in one element.
             * @param numIntervalsCheck Current number of intervals
             * @return True if we have added 1 interval.
             */
            public boolean addedOne(final int numIntervalsCheck) {
                return (numIntervals + 1 == numIntervalsCheck);
            }
        }

        DataCopy copy = new DataCopy(intervals.size());

        // Preconditions and invariant
        invariant();
        assert (copy != null) : "Datacopy not created, memory issue?";

        if (runningInterval == null) {

            runningInterval = new Interval(name, this);
            intervals.add(runningInterval);

            // Postcondition and invariant
            assert copy.addedOne(intervals.size()) : "Interval not created";
            invariant();

            return runningInterval;
        }

        // Postcondition and invariant
        invariant();
        assert !copy.addedOne(intervals.size()) : "Illegal interval created";

        return null;
    }

    /**
     * Stop the last (and presumed only because only one interval
     * should exist at once per task) interval running if there's
     * an interval running at all.
     * @return The duration of the last interval or null if there
     * isn't an interval running
     */
    @Override
    public Duration stopLastInterval() {

        // Precondition and invarant
        invariant();

        try {
            Duration lastDuration = runningInterval.stop();
            runningInterval = null;

            // Postconditions and invariant
            invariant();

            return lastDuration;
        } catch (Exception e) {
            log.warn("There is no interval running", e);
        }

        // Postconditions and invariant
        assert (runningInterval == null) : "Illegal interval has started";
        invariant();

        return null;
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
        //Precondition and invariant
        invariant();

        Duration temp = Duration.ofSeconds(0);
        try {

            if (toDate.isBefore(fromDate)) {
                throw new IllegalArgumentException(
                        "The dates are not coherent");
            } else {
                if (!(fromDate.isAfter(getEndTime())
                        || toDate.isBefore(getStartTime()))) {

                    for (Interval interval : intervals) {
                        temp = temp.plus(
                                interval.getDurationInRange(fromDate, toDate));
                    }
                }
            }
        } catch (NullPointerException e) {
            log.error("Null parameters", e);
        } catch (IllegalArgumentException e) {
            log.error("Argument with invalid values", e);
        }

        //Postcondition and invariant
        invariant();
        assert (temp != null) : "Invalid return";

        return temp;
    }

    /**
     * Accepts a visitor.
     */
    @Override
    public void acceptVisitor(final Visitor visitor) {

        // Preconditions and invariant
        invariant();

        visitor.visitTask(this);

        for (Interval interval : intervals) {
            interval.acceptVisitor(visitor);
        }

        // Postconditions and invariant
        invariant();
    }

    /**
     * Updates the duration of the object and,
     * if the object is not the last, call update on it's
     * parent.
     *
     * @param duration Increment of time.
     */
    @Override
    public void updateDuration(final Duration duration,
                               final LocalDateTime startTime,
                               final LocalDateTime endTime) {

        // Preconditions and invariant
        invariant();

        try {
            if (duration == null || startTime == null || endTime == null) {
                throw new IllegalArgumentException("Some parameter is null");
            } else {
                if (this.getStartTime() == null) {
                    this.setStartTime(startTime);
                }

                synchronized (this.getDuration()) {
                    this.setDuration(this.getDuration().plus(duration));
                    this.setEndTime(endTime);
                }

                super.updateDuration(duration, startTime, endTime);
            }
        } catch (IllegalArgumentException ex) {
            log.error("updateDuration parameters can't be null", ex);
        } catch (Exception ex) {
            log.error("Some error in the updateDuration method", ex);
        }

        // Postconditions and invariant
        invariant();
    }

    @Override
    public Boolean isRunningInterval() {
        return runningInterval != null;
    }

    @Override
    public Boolean addInterval(Interval interval){
        return intervals.add(interval);
    }
}
