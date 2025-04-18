/*
 * Copyright (c) 2019-2022 GeyserMC. http://geysermc.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * @author GeyserMC
 * @link https://github.com/GeyserMC/Geyser
 */

package org.geysermc.geyser.entity.type;

import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.data.entity.EntityDataTypes;
import org.cloudburstmc.protocol.bedrock.data.entity.EntityFlag;
import org.geysermc.geyser.entity.EntityDefinition;
import org.geysermc.geyser.entity.EntityDefinitions;
import org.geysermc.geyser.session.GeyserSession;
import org.geysermc.mcprotocollib.protocol.data.game.entity.metadata.EntityMetadata;
import org.geysermc.mcprotocollib.protocol.data.game.item.ItemStack;

import java.util.UUID;

/**
 * Used as a class for any projectile entity that looks like an item
 */
public class ThrowableItemEntity extends ThrowableEntity {
    /**
     * Number of draw ticks since the entity was spawned by the Java server
     */
    private int age;
    private boolean invisible;

    public ThrowableItemEntity(GeyserSession session, int entityId, long geyserId, UUID uuid, EntityDefinition<?> definition, Vector3f position, Vector3f motion, float yaw, float pitch, float headYaw) {
        super(session, entityId, geyserId, uuid, definition, position, motion, yaw, pitch, headYaw);
        setFlag(EntityFlag.INVISIBLE, true);
        invisible = false;
        age = 0;
    }

    @Override
    protected void initializeMetadata() {
        super.initializeMetadata();
        // Correct sizing
        dirtyMetadata.put(EntityDataTypes.SCALE, 0.5f);
    }

    private void checkVisibility() {
        age++;

        // Prevent projectiles from blocking the player's screen
        if (session.isTickingFrozen()) {
            // This may seem odd, but it matches java edition
            Vector3f playerPos = session.getPlayerEntity().getPosition().down(EntityDefinitions.PLAYER.offset());
            setInvisible(playerPos.distanceSquared(position.add(0, definition.offset(), 0)) < 12.25);
        } else {
            setInvisible(age < 2);
        }

        if (invisible != getFlag(EntityFlag.INVISIBLE)) {
            setFlag(EntityFlag.INVISIBLE, invisible);
            updateBedrockMetadata();
        }
    }

    @Override
    public void drawTick() {
        checkVisibility();
        super.drawTick();
    }

    @Override
    protected void setInvisible(boolean value) {
        invisible = value;
    }

    public void setItem(EntityMetadata<ItemStack, ?> entityMetadata) {
    }
}
