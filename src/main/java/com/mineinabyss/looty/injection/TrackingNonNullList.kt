package com.mineinabyss.looty.injection

import com.mineinabyss.idofront.messaging.broadcast
import net.minecraft.server.v1_16_R3.NonNullList
import org.bukkit.entity.Player

class TrackingNonNullList<E>(
    private val delegate: NonNullList<E>,
    private val player: Player
) : NonNullList<E>() {
    override fun get(index: Int): E {
        return delegate[index]
    }

    override fun set(index: Int, element: E): E {
        broadcast("Setting $element at $index (used to be ${get(index)}")
        return delegate.set(index, element)
    }

    override fun add(index: Int, element: E) {
        broadcast("Adding item at $index")
        delegate.add(index, element)
    }

    override fun removeAt(index: Int): E {
        broadcast("Removing item at $index")
        return delegate.removeAt(index)
    }

    override val size: Int
        get() = delegate.size

    override fun clear() {
        broadcast("Clearing inv")
        delegate.clear()
    }
}
