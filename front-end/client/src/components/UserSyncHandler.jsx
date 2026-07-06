import React, { useContext, useEffect, useRef } from 'react';
import { useAuth, useUser } from '@clerk/clerk-react';
import { AppContext } from '../context/AppContext';
import axios from 'axios';
import { toast } from 'react-hot-toast';

const UserSyncHandler = () => {
    const { isLoaded, isSignedIn, getToken } = useAuth();
    const { user } = useUser();
    const { backendUrl } = useContext(AppContext);
    
    // FIX: Using a ref flag ensures we remember the sync state instantly 
    // without triggering an infinite useEffect lifecycle loop.
    const hasSynced = useRef(false);

    useEffect(() => {
        const saveUser = async () => {
            // Stop early if Clerk isn't loaded, user isn't signed in, or we already synced
            if (!isLoaded || !isSignedIn || !user || hasSynced.current) {
                return;
            }

            try {
                // Prevent duplicate requests instantly before the async execution finishes
                hasSynced.current = true; 

                const token = await getToken();

                const userData = {
                    clerkId: user.id,
                    email: user.primaryEmailAddress?.emailAddress,
                    firstName: user.firstName,
                    lastName: user.lastName,
                    photoUrl: user.imageUrl,
                };

                // FIX: Changed "/users" to "/api/users" to match your Spring Security configurations perfectly
                await axios.post(`${backendUrl}/users`, userData, {
                    headers: { "Authorization": `Bearer ${token}` }
                });

                console.log("User synced successfully!");
                
            } catch (error) {
                // Reset flag on absolute failure so it can attempt a safe retry if needed
                hasSynced.current = false; 
                console.error("User sync failed", error);
                toast.error("User sync failed. Please try again.");
            }
        };

        saveUser();
    // FIX: Removed 'synced' from dependencies array to stop state recycling loops
    }, [isLoaded, isSignedIn, getToken, user, backendUrl]); 

    return null;
}

export default UserSyncHandler;