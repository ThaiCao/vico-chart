package pl.patrykgoworowski.liftchart_common.data_set.entry

import android.util.Log
import pl.patrykgoworowski.liftchart_common.data_set.AnyEntry
import pl.patrykgoworowski.liftchart_common.data_set.entry.EntryCollection.Companion.NO_VALUE
import pl.patrykgoworowski.liftchart_common.entry.FloatEntry
import kotlin.math.abs
import kotlin.math.min


class EntryList<T : AnyEntry>() : EntryCollection<T> {

    override val entries: ArrayList<AnyEntry> = ArrayList()

    private var _minX: Float = NO_VALUE
    private var _maxX: Float = NO_VALUE
    private var _minY: Float = NO_VALUE
    private var _maxY: Float = NO_VALUE
    private var _step: Float = NO_VALUE

    override val minX: Float by this::_minX
    override val maxX: Float by this::_maxX
    override val minY: Float by this::_minY
    override val maxY: Float by this::_maxY
    override val step: Float by this::_step

    constructor(entries: Collection<T>) : this() {
        this.entries.addAll(entries)
        recalculateMinMax()
    }

    constructor(vararg entries: T) : this() {
        this.entries.addAll(entries)
        recalculateMinMax()
    }

    constructor(vararg entries: Pair<Number, Number>) : this() {
        this.entries.addAll(
            entries.map { (x, y) ->
                Log.d("Test", "${hashCode()} adding $x, $y")
                FloatEntry(x.toFloat(), y.toFloat())
            }
        )
        recalculateMinMax()
        Log.d("Test", "${hashCode()} added ${this.entries.size}")
    }

    override fun setEntries(entries: Collection<T>) {
        this.entries.clear()
        this.entries.addAll(entries)
        recalculateMinMax()
    }

    override fun setEntries(vararg entries: T) {
        setEntries(ArrayList(entries.toList()))
    }

    override fun plusAssign(entry: AnyEntry) {
        entries += entry
        recalculateMinMax(entry)
    }

    override fun plusAssign(entries: Collection<AnyEntry>) {
        this.entries.addAll(entries)
        recalculateMinMax(entries)
    }

    override fun minusAssign(entry: AnyEntry) {
        entries -= entry
        recalculateMinMax()
    }

    override fun minusAssign(entries: Collection<AnyEntry>) {
        this.entries.removeAll(entries)
        recalculateMinMax()
    }

    private fun recalculateMinMax(entries: Collection<AnyEntry> = this.entries) {
        entries.forEach { entry ->
            recalculateMinMax(entry)
        }
    }

    private fun recalculateMinMax(entry: AnyEntry) {
        _minX = if (_minX == NO_VALUE) entry.x else _minX.coerceAtMost(entry.x)
        _maxX = _maxX.coerceAtLeast(entry.x)
        _minY = if (_minY == NO_VALUE) entry.y else _minY.coerceAtMost(entry.y)
        _maxY = _maxY.coerceAtLeast(entry.y)

        if (entries.size > 1) {
            for (index in 1..entries.lastIndex) {
                val difference = abs(entries[index].x - entries[index - 1].x)
                _step = if (_step == NO_VALUE) difference else min(_step, difference)
            }
        } else {
            _step = 1f
        }
    }

}
